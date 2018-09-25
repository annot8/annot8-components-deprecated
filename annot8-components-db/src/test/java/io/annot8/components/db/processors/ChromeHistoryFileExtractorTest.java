package io.annot8.components.db.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import io.annot8.common.data.content.FileContent;
import io.annot8.common.implementations.registries.ContentBuilderFactoryRegistry;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.ProcessorResponse.Status;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.testing.testimpl.TestAnnotationStoreFactory;
import io.annot8.testing.testimpl.TestContentBuilderFactoryRegistry;
import io.annot8.testing.testimpl.TestGroupStore;
import io.annot8.testing.testimpl.TestItem;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ChromeHistoryFileExtractorTest {

  @Test
  public void testProcess() {
    ContentBuilderFactoryRegistry registry = new TestContentBuilderFactoryRegistry();
    registry.register(URLContent.class, new DefaultURL.BuilderFactory(new TestAnnotationStoreFactory()));
    List<Item> createdItems = new ArrayList<>();

    ItemFactory itemFactory = new ItemFactory(){
      @Override
      public Item create() {
        TestItem childItem = new TestItem(null, new TestGroupStore(), registry);
        createdItems.add(childItem);
        return childItem;
      }
    };

    Item item = new TestItem(itemFactory, new TestGroupStore(), registry);
    FileContent fileContent = Mockito.mock(FileContent.class);
    ((TestItem) item).setContent(Collections.singletonMap("file", fileContent));

    URL resource = ChromeHistoryFileExtractorTest.class.getClassLoader().getResource("ChromeHistory");
    File file = null;
    try {
      file = new File(resource.toURI());
    } catch (URISyntaxException e) {
      fail("Error not expected when finding test file");
    }
    when(fileContent.getData()).thenReturn(file);

    ChromeHistoryFileExtractor extractor = new ChromeHistoryFileExtractor();
    ProcessorResponse response = null;
    try {
      response = extractor.process(item);
    } catch (Annot8Exception e) {
      fail("The test should not error here.", e);
    }

    assertEquals(Status.OK, response.getStatus());
    assertEquals(3, createdItems.size());

    createdItems.stream().flatMap(Item::getContents).forEach(c -> {
      assertEquals("url", c.getName());
      assertNotNull(c.getId());
      assertNotNull(c.getProperties());
      assertNotNull(c.getAnnotations());
    });

    Stream <String> urls = createdItems.stream()
        .flatMap(Item::getContents)
        .map(Content::getData)
        .map(URL.class::cast)
        .map(URL::toExternalForm);

    assertThat(urls).containsExactlyInAnyOrder("https://www.google.com/webhp?ie=UTF-8&rct=j",
        "https://www.bbc.co.uk/", "https://www.reddit.com/");
  }

}