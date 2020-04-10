package cc.fans.miot.module;

import io.vertx.core.json.JsonObject;

public class Device {
  private String id;
  private String name;
  private String type;
  private String token;

  public Device() {
  }

  public Device(String id, String name, String type, String token) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.token = token;
  }

  public JsonObject toConduitJson(){
    return new JsonObject()
      .put("id", this.id)
      .put("name", this.name)
      .put("type", this.type)
      .put("token", this.token);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
