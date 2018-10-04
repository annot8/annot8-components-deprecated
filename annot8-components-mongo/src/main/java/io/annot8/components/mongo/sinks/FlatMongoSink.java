/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.mongo.sinks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.annot8.components.mongo.data.AnnotationDto;
import io.annot8.components.mongo.data.ContentDto;
import io.annot8.components.mongo.data.ItemDto;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;

public class FlatMongoSink extends AbstractMongoSink {

  private static final String ITEM = "item";
  private static final String CONTENT = "content";
  private static final String ANNOTATION = "annotation";
  private MongoCollection<Document> itemCollection;
  private MongoCollection<Document> contentsCollection;
  private MongoCollection<Document> annotationsCollection;

  @Override
  public void storeItem(Item item) throws Annot8Exception {
    String parentId = null;
    if (item.getParent().isPresent()) {
      parentId = item.getParent().get();
    }
    ItemDto itemDto = new ItemDto(item.getId(), parentId, item.getProperties().getAll(), null);

    Collection<ContentDto> contents =
        item.getContents()
            .map(
                c ->
                    new ContentDto(
                        c.getId(),
                        c.getName(),
                        c.getData(),
                        c.getProperties().getAll(),
                        null,
                        item.getId()))
            .collect(Collectors.toList());

    Collection<AnnotationDto> annotations =
        item.getContents().flatMap(this::getAnnotations).collect(Collectors.toList());

    Document itemDocument = null;
    List<Document> contentDocuments = null;
    List<Document> annotationDocuments = null;
    try {
      itemDocument = toMongoDocument(itemDto);
      contentDocuments = new ArrayList<>();
      annotationDocuments = new ArrayList<>();
      for (ContentDto content : contents) {
        contentDocuments.add(toMongoDocument(content));
      }
      for (AnnotationDto annotation : annotations) {
        annotationDocuments.add(toMongoDocument(annotation));
      }
    } catch (JsonProcessingException e) {
      log().error("Error converting object to document", e);
      throw new Annot8Exception("Error storing item", e);
    }

    itemCollection.insertOne(itemDocument);
    contentsCollection.insertMany(contentDocuments);
    annotationsCollection.insertMany(annotationDocuments);
  }

  private Stream<AnnotationDto> getAnnotations(Content content) {
    return content.getAnnotations().getAll().map(a -> getAnnotation(a, content));
  }

  private AnnotationDto getAnnotation(Annotation annotation, Content content) {
    Object data = null;
    Optional optionalData = annotation.getBounds().getData(content);
    if (optionalData.isPresent()) {
      data = optionalData.get();
    }
    return new AnnotationDto(
        annotation.getId(),
        annotation.getType(),
        annotation.getBounds(),
        data,
        annotation.getProperties().getAll(),
        content.getId());
  }

  @Override
  protected void configureMongo(MongoConnection connection) {
    MongoDatabase database = connection.getDatabase();
    itemCollection = database.getCollection(ITEM);
    contentsCollection = database.getCollection(CONTENT);
    annotationsCollection = database.getCollection(ANNOTATION);
  }
}
