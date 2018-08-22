package io.annot8.components.mongo.sources;

import io.annot8.core.settings.Settings;

public class MongoSourceSettings implements Settings {
  private String collection = "input";
  private String connectionResourceKey = null;

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public String getConnectionResourceKey() {
    return connectionResourceKey;
  }

  public void setConnectionResourceKey(String connectionResourceKey) {
    this.connectionResourceKey = connectionResourceKey;
  }

  public boolean hasConnectionResourceKey() {
    return connectionResourceKey != null && !connectionResourceKey.isEmpty();
  }
}
