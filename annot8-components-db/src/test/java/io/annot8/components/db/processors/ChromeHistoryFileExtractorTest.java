/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.annot8.common.data.content.FileContent;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.ProcessorResponse.Status;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestGroupStore;
import io.annot8.testing.testimpl.TestItem;
import io.annot8.testing.testimpl.TestItemFactory;

public class ChromeHistoryFileExtractorTest {

  @Test
  public void testProcess() throws BadConfigurationException, MissingResourceException {
    final TestContext testContext = new TestContext();

    TestItem item = new TestItem(new TestGroupStore());
    TestItemFactory itemFactory = (TestItemFactory) item.getItemFactory();
    FileContent fileContent = mockFileContent("ChromeHistory");
    ((TestItem) item).setContent(Collections.singletonMap("file", fileContent));

    ChromeHistoryFileExtractor extractor = new ChromeHistoryFileExtractor();
    extractor.configure(testContext);
    ProcessorResponse response = null;
    try {
      response = extractor.process(item);
    } catch (Annot8Exception e) {
      fail("The test should not error here.", e);
    }

    List<Item> createdItems = itemFactory.getCreatedItems();

    assertEquals(Status.OK, response.getStatus());
    assertEquals(3, createdItems.size());

    createdItems
        .stream()
        .flatMap(Item::getContents)
        .forEach(
            c -> {
              assertEquals("url", c.getName());
              assertNotNull(c.getId());
              assertNotNull(c.getProperties());
              assertNotNull(c.getAnnotations());
            });

    Stream<String> urls =
        createdItems
            .stream()
            .flatMap(Item::getContents)
            .map(Content::getData)
            .map(URL.class::cast)
            .map(URL::toExternalForm);

    assertThat(urls)
        .containsExactlyInAnyOrder(
            "https://www.google.com/webhp?ie=UTF-8&rct=j",
            "https://www.bbc.co.uk/",
            "https://www.reddit.com/");
  }

  @Test
  public void testNonDBFile() {
    // Test to ensure that normal files are not processed
    // and do not result in a failed process
    Item item = new TestItem();
    FileContent content = mockFileContent("nonSqlliteFile.txt");
    ((TestItem) item).setContent(Collections.singletonMap("file", content));
    ChromeHistoryFileExtractor extractor = new ChromeHistoryFileExtractor();
    ProcessorResponse response = null;
    try {
      response = extractor.process(item);
    } catch (Annot8Exception e) {
      fail("The test is not expected to fail here");
    }
    assertEquals(Status.OK, response.getStatus());
  }

  @Test
  public void testFailingFile() {
    Item item = new TestItem();
    FileContent content = Mockito.mock(FileContent.class);
    when(content.getData()).thenReturn(new File("nonExistentFile"));
    ((TestItem) item).setContent(Collections.singletonMap("file", content));
    ChromeHistoryFileExtractor fileExtractor = new ChromeHistoryFileExtractor();
    ProcessorResponse response = null;
    try {
      response = fileExtractor.process(item);
    } catch (Annot8Exception e) {
      fail("Test should not error here", e);
    }
    assertEquals(Status.ITEM_ERROR, response.getStatus());
  }

  private FileContent mockFileContent(String resourceFileName) {
    FileContent fileContent = Mockito.mock(FileContent.class);
    URL resource =
        ChromeHistoryFileExtractorTest.class.getClassLoader().getResource(resourceFileName);
    File file = null;
    try {
      file = new File(resource.toURI());
    } catch (URISyntaxException e) {
      fail("Error not expected when finding test file");
    }
    when(fileContent.getData()).thenReturn(file);
    return fileContent;
  }
}
