package io.annot8.components.mongo.sinks;


import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCollection;
import io.annot8.common.data.bounds.NoBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.ProcessorResponse.Status;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestItem;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NestedItemSinkTest {

  @Test
  public void testStore(){
    MongoConnection connection = Mockito.mock(MongoConnection.class);
    MongoCollection collection = Mockito.mock(MongoCollection.class);
    when(connection.getCollection()).thenReturn(collection);
    NestedItemSink store = new NestedItemSink(collection);
    store.configure(new TestContext(), connection);

    Item item = new TestItem();
    Content content = null;
    Annotation annotation = null;
    try {
      content = item
          .create(Text.class)
          .withName("test")
          .withData("testing")
          .save();
      annotation = content.getAnnotations()
          .create()
          .withBounds(NoBounds.getInstance())
          .withType("test")
          .save();
    } catch (UnsupportedContentException | IncompleteException e) {
      fail("Test should not error here", e);
    }

    ProcessorResponse response = store.process(item);
    assertEquals(Status.OK, response.getStatus());
    String expected = getExpected(item.getId(), content.getId(), annotation.getId());
    System.out.println(expected);
    Document expectedDoc = Document.parse(expected);
    Mockito.verify(collection, Mockito.times(1))
        .insertOne(Mockito.eq(expectedDoc));
  }

  private String getExpected(String itemId, String contentId, String annotationId){
    return "{"
        + "\"id\":\"" + itemId + "\","
        + "\"parentId\":null,"
        + "\"properties\":{},"
        + "\"contents\":["
          + "{"
          + "\"id\":\"" + contentId + "\","
          + "\"itemId\":\"" + itemId + "\""
          + "\"name\":\"test\","
          + "\"data\":\"testing\","
          + "\"properties\":{},"
          + "\"annotations\":["
            + "{"
            + "\"id\":\"" + annotationId + "\","
            + "\"type\":\"test\","
            + "\"properties\":{},"
            + "\"bounds\":{},"
            + "\"data\":null,"
            + "\"contentId\":\"" + contentId + "\"}]}]}";
  }

}
