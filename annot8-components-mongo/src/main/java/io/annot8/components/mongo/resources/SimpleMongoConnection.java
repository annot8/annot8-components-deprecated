/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.mongo.resources;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SimpleMongoConnection implements MongoConnection {

  private MongoClient client;
  private MongoDatabase database;
  private MongoCollection<Document> collection;

  public SimpleMongoConnection(
      MongoClient client, MongoDatabase database, MongoCollection<Document> collection) {
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
    if (client != null) {
      client.close();
    }
  }
}
