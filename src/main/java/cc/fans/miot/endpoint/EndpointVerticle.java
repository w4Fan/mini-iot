package cc.fans.miot.endpoint;

import cc.fans.miot.module.Device;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class EndpointVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    Router baseRouter = Router.router(vertx);
    Router apiRouter = Router.router(vertx);

    baseRouter.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response.putHeader("Content-Type", "text/plain").end("Hello Mini IoT with Vert.x !");
    });

    apiRouter.route("/device*").handler(BodyHandler.create());
    apiRouter.post("/devices").handler(this::registerDevice);
    baseRouter.mountSubRouter("/api", apiRouter);

    vertx.createHttpServer()
      .requestHandler(baseRouter)
      .listen(8000, result -> {
        if (result.succeeded()) {
          startFuture.complete();
        } else {
          startFuture.fail(result.cause());
        }
      });
  }

  private void registerDevice(RoutingContext routingContext) {
    JsonObject message = new JsonObject()
      .put("action", "register-device")
      .put("device", routingContext.getBodyAsJson());

    vertx.eventBus().request("persistence", message, ar -> {
      if (ar.succeeded()) {
        Device device = Json.decodeValue(ar.result().body().toString(), Device.class);
        routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(device.toConduitJson()));
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(ar.cause().getMessage()));
      }
    });
  }
}
