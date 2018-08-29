package io.annot8.components.mongo.resources;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.annot8.components.base.components.AbstractResource;
import io.annot8.core.capabilities.UsesResource;
import io.annot8.core.components.Resource;
import io.annot8.core.context.Context;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.settings.SettingsClass;
import java.util.Optional;
import org.bson.Document;

public class SimpleMongoConnection implements MongoConnection {

  private MongoClient client;
  private MongoDatabase database;
  private MongoCollection<Document> collection;

  public SimpleMongoConnection(MongoClient client, MongoDatabase database, MongoCollection<Document> collection) {
    this.client = client;
    this.database = database;
    this.collection = collection;
  }

  public MongoDatabase getDatabase() {
    return database;
  }

  public <T> MongoCollection getCollection(Class<T> clazz) {
    return getCollection().withDocumentClass(clazz);
  }

  public MongoCollection getCollection() {
    return collection;
  }

  public void disconnect() {
    if(client != null) {
      client.close();
    }
  }
}
