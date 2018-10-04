/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.mongo.sinks;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.ProcessorResponse.Status;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;
import io.annot8.testing.testimpl.TestAnnotationStore;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestItem;
import io.annot8.testing.testimpl.TestProperties;

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

    FlatMongoSink store = new FlatMongoSink();
    store.configure(new TestContext(), connection);
    Item item = new TestItem();
    Content content = null;
    Annotation ann1 = null;
    Annotation ann2 = null;
    try {
      content = item.create(Text.class).withName("test").withData("testing").save();
      ann1 =
          content
              .getAnnotations()
              .create()
              .withBounds(new SpanBounds(0, 1))
              .withType("test")
              .save();
      ann2 =
          content
              .getAnnotations()
              .create()
              .withBounds(new SpanBounds(1, 2))
              .withType("test2")
              .save();
    } catch (UnsupportedContentException | IncompleteException e) {
      fail("Test should not error here", e);
    }

    ProcessorResponse response = store.process(item);
    assertEquals(ProcessorResponse.Status.OK, response.getStatus());

    Document expectedItem = getExpecetedItem(item.getId());
    Document expectedContent = getExpectedContent(content.getId(), item.getId());
    Document expectedAnn1 =
        getExpectedAnnotation(ann1.getId(), content.getId(), ann1.getType(), "t", 0, 1);
    Document expectedAnn2 =
        getExpectedAnnotation(ann2.getId(), content.getId(), ann2.getType(), "e", 1, 2);
    List<Document> expectedAnnotations = new ArrayList<>();
    expectedAnnotations.add(expectedAnn1);
    expectedAnnotations.add(expectedAnn2);
    ListArgumentMatcher matchesDocs = new ListArgumentMatcher(expectedAnnotations);

    Mockito.verify(itemStore, Mockito.times(1)).insertOne(expectedItem);
    Mockito.verify(contentStore, Mockito.times(1))
        .insertMany(Collections.singletonList(expectedContent));
    Mockito.verify(annotationStore, Mockito.times(1)).insertMany(Mockito.argThat(matchesDocs));
  }

  @Test
  public void testProcessNonSerializableData() {
    MongoConnection connection = Mockito.mock(MongoConnection.class);
    MongoDatabase database = Mockito.mock(MongoDatabase.class);
    MongoCollection itemStore = Mockito.mock(MongoCollection.class);
    MongoCollection contentStore = Mockito.mock(MongoCollection.class);
    MongoCollection annotationStore = Mockito.mock(MongoCollection.class);

    when(connection.getDatabase()).thenReturn(database);
    when(database.getCollection(Mockito.eq("item"))).thenReturn(itemStore);
    when(database.getCollection(Mockito.eq("content"))).thenReturn(contentStore);
    when(database.getCollection(Mockito.eq("annotation"))).thenReturn(annotationStore);

    FlatMongoSink store = new FlatMongoSink();
    store.configure(new TestContext(), connection);

    TestItem item = new TestItem();
    Content content = Mockito.mock(Content.class);
    when(content.getId()).thenReturn("test");
    when(content.getName()).thenReturn("test");
    when(content.getAnnotations()).thenReturn(new TestAnnotationStore());
    when(content.getData()).thenReturn(new NonSerializableTestData("test"));
    when(content.getProperties()).thenReturn(new TestProperties());
    item.setContent(Collections.singletonMap("content", content));

    ProcessorResponse response = store.process(item);
    assertEquals(Status.ITEM_ERROR, response.getStatus());
    Mockito.verify(itemStore, times(0)).insertOne(Mockito.any());
    Mockito.verify(contentStore, times(0)).insertMany(Mockito.any());
    Mockito.verify(annotationStore, times(0)).insertMany(Mockito.any());
  }

  private Document getExpecetedItem(String itemId) {
    String json =
        "{"
            + "\"id\":\""
            + itemId
            + "\","
            + "\"parentId\":null,"
            + "\"properties\":{},"
            + "\"contents\":null"
            + "}";
    return Document.parse(json);
  }

  private Document getExpectedContent(String contentId, String itemId) {
    String json =
        "{"
            + "\"id\":\""
            + contentId
            + "\","
            + "\"itemId\":\""
            + itemId
            + "\""
            + "\"name\":\"test\","
            + "\"data\":\"testing\","
            + "\"properties\":{},"
            + "\"annotations\":null"
            + "}";
    return Document.parse(json);
  }

  private Document getExpectedAnnotation(
      String annotationId, String contentId, String type, String data, int begin, int end) {
    String json =
        "{"
            + "\"id\":\""
            + annotationId
            + "\","
            + "\"type\":\""
            + type
            + "\","
            + "\"properties\":{},"
            + "\"bounds\":{\"begin\":"
            + begin
            + ", \"end\":"
            + end
            + "},"
            + "\"data\":\""
            + data
            + "\","
            + "\"contentId\":\""
            + contentId
            + "\""
            + "}";
    return Document.parse(json);
  }

  private class ListArgumentMatcher implements ArgumentMatcher<List<Document>> {

    private List<Document> documents;

    public ListArgumentMatcher(List<Document> documents) {
      this.documents = documents;
    }

    @Override
    public boolean matches(List<Document> argument) {
      if (argument == null) {
        return false;
      }
      if (documents.size() != argument.size()) {
        return false;
      }
      for (Document doc : argument) {
        if (!documents.contains(doc)) {
          return false;
        }
      }

      return true;
    }
  }
}
