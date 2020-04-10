package cc.fans.miot.persistence;

import cc.fans.miot.module.Device;
import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class PersistenceVerticle extends AbstractVerticle {

  private CassandraClient client;

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    CassandraClientOptions options = new CassandraClientOptions()
      .setKeyspace("miot");
    client = CassandraClient.create(vertx, options);

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

  private void registerDevice(Message<JsonObject> message) {
    Device device = Json.decodeValue(message.body().getJsonObject("device").toString(), Device.class);
    device.setId("1");
    device.setToken("cc.fans.device");
    message.reply(Json.encodePrettily(device.toConduitJson()));
  }
}
