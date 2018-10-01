package io.annot8.components.mongo.resources;

public class DefaultMongoConnectionSettings extends MongoConnectionSettings {

  public DefaultMongoConnectionSettings() {
    this.setConnection("mongodb://localhost");
    this.setDatabase("annot8");
  }

}
