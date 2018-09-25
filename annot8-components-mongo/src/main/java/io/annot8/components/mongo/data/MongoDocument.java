/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.mongo.data;

import org.bson.Document;

import io.annot8.core.data.Content;
import io.annot8.core.properties.ImmutableProperties;
import io.annot8.core.stores.AnnotationStore;

public class MongoDocument implements Content<Document> {

  private final String id;
  private final String name;
  private final AnnotationStore annotations;
  private final ImmutableProperties properties;
  private final Document document;

  public MongoDocument(
      String id,
      String name,
      AnnotationStore annotations,
      ImmutableProperties properties,
      Document document) {
    this.id = id;
    this.name = name;
    this.annotations = annotations;
    this.properties = properties;
    this.document = document;
  }

  @Override
  public Document getData() {
    return document;
  }

  @Override
  public Class<Document> getDataClass() {
    return Document.class;
  }

  @Override
  public Class<? extends Content<Document>> getContentClass() {
    return MongoDocument.class;
  }

  @Override
  public AnnotationStore getAnnotations() {
    return annotations;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ImmutableProperties getProperties() {
    return properties;
  }
}
