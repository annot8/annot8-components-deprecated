package io.annot8.components.mongo.sinks;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.annot8.common.data.bounds.NoBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestItem;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FlatItemSinkTest {

  @Test
  public void testStore() {
    MongoConnection connection = Mockito.mock(MongoConnection.class);
    MongoDatabase database = Mockito.mock(MongoDatabase.class);
    MongoCollection itemStore = Mockito.mock(MongoCollection.class);
    MongoCollection contentStore = Mockito.mock(MongoCollection.class);
    MongoCollection annotationStore = Mockito.mock(MongoCollection.class);

    when(connection.getDatabase()).thenReturn(database);
    when(database.getCollection(Mockito.eq("item"))).thenReturn(itemStore);
    when(database.getCollection(Mockito.eq("content"))).thenReturn(contentStore);
    when(database.getCollection(Mockito.eq("annotation"))).thenReturn(annotationStore);

    FlatMongoSink store = new FlatMongoSink(itemStore, contentStore, annotationStore);
    store.configure(new TestContext(), connection);
    Item item = new TestItem();
    Content content = null;
    Annotation ann1 = null;
    Annotation ann2 = null;
    try {
      content = item
          .create(Text.class)
          .withName("test")
          .withData("testing")
          .save();
      ann1 = content.getAnnotations()
          .create()
          .withBounds(NoBounds.getInstance())
          .withType("test")
          .save();
      ann2 = content.getAnnotations()
          .create()
          .withBounds(NoBounds.getInstance())
          .withType("test2")
          .save();
    } catch (UnsupportedContentException | IncompleteException e) {
      fail("Test should not error here", e);
    }

    ProcessorResponse response = store.process(item);
    assertEquals(ProcessorResponse.Status.OK, response.getStatus());

    Document expectedItem = getExpecetedItem(item.getId());
    Document expectedContent = getExpectedContent(content.getId(), item.getId());
    Document expectedAnn1 = getExpectedAnnotation(ann1.getId(), content.getId(), ann1.getType());
    Document expectedAnn2 = getExpectedAnnotation(ann2.getId(), content.getId(), ann2.getType());

    Mockito.verify(itemStore, Mockito.times(1)).insertOne(expectedItem);
    Mockito.verify(contentStore, Mockito.times(1)).insertOne(expectedContent);
    Mockito.verify(annotationStore, Mockito.times(2)).insertOne(Mockito.any());
    Mockito.verify(annotationStore, Mockito.times(1)).insertOne(expectedAnn1);
    Mockito.verify(annotationStore, Mockito.times(1)).insertOne(expectedAnn2);
  }

  private Document getExpecetedItem(String itemId){
    String json = "{"
        + "\"id\":\"" + itemId + "\","
        + "\"parentId\":null,"
        + "\"properties\":{},"
        + "\"contents\":null"
        + "}";
    return Document.parse(json);
  }

  private Document getExpectedContent(String contentId, String itemId){
    String json = "{"
        + "\"id\":\"" + contentId + "\","
        + "\"itemId\":\"" + itemId + "\""
        + "\"name\":\"test\","
        + "\"data\":\"testing\","
        + "\"properties\":{},"
        + "\"annotations\":null"
        + "}";
    return Document.parse(json);
  }

  private Document getExpectedAnnotation(String annotationId, String contentId, String type){
    String json =  "{"
        + "\"id\":\"" + annotationId + "\","
        + "\"type\":\"" + type + "\","
        + "\"properties\":{},"
        + "\"bounds\":{},"
        + "\"data\":null,"
        + "\"contentId\":\"" + contentId + "\""
        + "}";
    return Document.parse(json);
  }

}

