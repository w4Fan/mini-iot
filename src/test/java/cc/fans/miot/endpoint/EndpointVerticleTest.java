package cc.fans.miot.endpoint;

import cc.fans.miot.MainVerticle;
import cc.fans.miot.module.DeviceType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class EndpointVerticleTest {

  private Vertx vertx;
  private WebClient client;

  @Before
  public void setUp(TestContext tc) {
    vertx = Vertx.vertx();
    client = WebClient.create(vertx);
    vertx.deployVerticle(new MainVerticle());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close();
  }

  @Test
  public void testRegisterDevice(TestContext tc) {
    Async async = tc.async();
    JsonObject device = new JsonObject()
      .put("name", "device")
      .put("type", DeviceType.DEVICE.name());

    client
      .post(8000, "localhost", "/api/devices")
      .putHeader("Content-Type", "application/json")
      .sendJsonObject(device, ar -> {
        if (ar.succeeded()) {
          tc.assertEquals(200, ar.result().statusCode());
          JsonObject returnedJson = ar.result().bodyAsJsonObject();
          tc.assertNotNull(returnedJson.getString("id"));
          tc.assertNotNull(returnedJson.getString("token"));
          tc.assertEquals(device.getString("name"), returnedJson.getString("name"));
          tc.assertEquals(device.getString("type"), returnedJson.getString("type"));
          async.complete();
        } else {
          tc.assertTrue(ar.succeeded());
          async.complete();
        }
      });
  }
}
