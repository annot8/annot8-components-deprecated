package io.annot8.components.mongo.resources;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.annot8.core.components.Resource;

public class Mongo implements Resource {

  private final MongoClient client;
  private final MongoDatabase database;

  public Mongo(String connectionString, String database){
    this.client = MongoClients.create(connectionString);
    this.database = client.getDatabase(database);
  }

  public Mongo(ConnectionString connectionString, String database){
    this.client = MongoClients.create(connectionString);
    this.database = client.getDatabase(database);
  }

  public Mongo(MongoClientSettings settings, String database){
    this.client = MongoClients.create(settings);
    this.database = client.getDatabase(database);
  }

  public MongoDatabase getDatabase() {
    return database;
  }

  @Override
  public void close() {
    client.close();
  }
}
