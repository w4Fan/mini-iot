package cc.fans.miot.persistence;

import cc.fans.miot.module.Device;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class PersistenceVerticle extends AbstractVerticle {

  private MongoClient mongoClient;

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    connectMongoDb();
    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer("persistence");

    consumer.handler(message -> {
      String action = message.body().getString("action");
      switch (action) {
        case "register-device":
          registerDevice(message);
          break;
        default:
          message.fail(1, "Unkown Action: " + message.body());
      }
    });
  }

  private void connectMongoDb() {
    JsonObject config = new JsonObject()
        .put("db_name", "admin")
        .put("host", "localhost")
        .put("port", 27017)
        .put("username", "miot")
        .put("password", "abcd1234");
    System.setProperty("org.mongodb.async.type", "netty");
    mongoClient = MongoClient.createShared(vertx, config, "MONGO_POOL");
  }

  private void registerDevice(Message<JsonObject> message) {
    Device device = Json.decodeValue(message.body().getJsonObject("device").toString(), Device.class);
    device.setToken("cc.fans.device");
    mongoClient.insert("device", device.toConduitJson(), res -> {
      if (res.succeeded()) {
        String id = res.result();
        device.setId(id);
        message.reply(Json.encodePrettily(device.toConduitJson()));
      } else {
        message.fail(2, "Insert failed: " + res.cause().getMessage());
      }
    });
  }
}
