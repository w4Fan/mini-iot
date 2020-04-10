package cc.fans.miot;

import cc.fans.miot.endpoint.EndpointVerticle;
import cc.fans.miot.persistence.PersistenceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    CompositeFuture.all(
      deployVerticle(EndpointVerticle.class.getName()),
      deployVerticle(PersistenceVerticle.class.getName())
    ).onComplete(f -> {
      if (f.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(f.cause());
      }
    });
  }

  Future<Void> deployVerticle(String verticleName) {
    Promise<Void> retVal = Promise.promise();
    vertx.deployVerticle(verticleName, event -> {
      if (event.succeeded()) {
        retVal.complete();
      } else {
        retVal.fail(event.cause());
      }
    });
    return retVal.future();
  }
}
