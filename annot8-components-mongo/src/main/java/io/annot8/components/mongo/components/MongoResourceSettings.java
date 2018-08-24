package io.annot8.components.mongo.components;

import io.annot8.components.mongo.resources.MongoConnectionSettings;
import io.annot8.core.settings.Settings;

public class MongoResourceSettings implements Settings {
  private String mongo = null;

  public String getMongo() {
    return mongo;
  }

  public void getMongo(String mongo) {
    this.mongo = mongo;
  }

  public void setMongo(String mongo) {
    this.mongo = mongo;
  }

  public boolean hasMongo() {
    return mongo != null && !mongo.isEmpty();
  }

  @Override
  public boolean validate() {
    return true;
  }
}
