/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.files.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.annot8.common.data.content.FileContent;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.ProcessorResponse.Status;
import io.annot8.core.data.Item;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.testing.testimpl.TestAnnotationStore;

public class FileMetadataExtractorTest {

  @Test
  public void testProcess() {
    Item item = Mockito.mock(Item.class);
    FileContent fileContent = Mockito.mock(FileContent.class);
    AnnotationStore store = new TestAnnotationStore();

    URL resource = FileMetadataExtractorTest.class.getResource("testfilemetadata.txt");
    File file = null;
    try {
      file = new File(resource.toURI());
    } catch (URISyntaxException e) {
      fail("Error not expected when finding test file");
    }
    when(fileContent.getData()).thenReturn(file);
    when(fileContent.getAnnotations()).thenReturn(store);

    doAnswer(
            new Answer<Stream<FileContent>>() {
              @Override
              public Stream<FileContent> answer(InvocationOnMock invocation) {
                return Stream.of(fileContent);
              }
            })
        .when(item)
        .getContents(Mockito.eq(FileContent.class));

    FileMetadataExtractor extractor = new FileMetadataExtractor();

    ProcessorResponse processResponse = extractor.process(item);

    assertEquals(Status.OK, processResponse.getStatus());

    List<Annotation> annotations =
        fileContent.getAnnotations().getAll().collect(Collectors.toList());
    assertEquals(file.getAbsolutePath(), getKeyValue(annotations, FileMetadata.PATH));
    assertEquals("txt", getKeyValue(annotations, FileMetadata.EXTENSION));
    assertFalse((boolean) getKeyValue(annotations, FileMetadata.HIDDEN));
    assertTrue((boolean) getKeyValue(annotations, FileMetadata.REGULAR));
    assertFalse((boolean) getKeyValue(annotations, FileMetadata.SYM_LINK));
    assertNotNull(getKeyValue(annotations, FileMetadata.DATE_CREATED));
    assertNotNull(getKeyValue(annotations, FileMetadata.LAST_MODIFIED));
    assertNotNull(getKeyValue(annotations, FileMetadata.LAST_ACCESS_DATE));
    assertEquals(60L, getKeyValue(annotations, FileMetadata.FILE_SIZE));
    assertNotNull(getKeyValue(annotations, FileMetadata.OWNER));
    assertFalse((boolean) getKeyValue(annotations, FileMetadata.DIRECTORY));
    annotations.forEach(a -> assertEquals(FileMetadataExtractor.FILE_METADATA, a.getType()));
  }

  private Object getKeyValue(List<Annotation> annotations, String key) {
    for (Annotation annotation : annotations) {
      if (annotation.getProperties().has(key)) {
        return annotation.getProperties().get(key).get();
      }
    }
    fail("Key: " + key + " not found in the provided list");
    return null;
  }

  @Test
  public void testProcessNoFileContent() {
    Item item = Mockito.mock(Item.class);
    when(item.getContents(Mockito.eq(FileContent.class))).thenReturn(Stream.empty());

    FileMetadataExtractor extractor = new FileMetadataExtractor();
    ProcessorResponse processResponse = extractor.process(item);

    assertEquals(Status.OK, processResponse.getStatus());
  }

  @Test
  public void testFileNotExisting() {
    Item item = Mockito.mock(Item.class);
    FileContent fileContent = Mockito.mock(FileContent.class);
    when(fileContent.getData()).thenReturn(new File("nonExistentFile"));
    doAnswer(
            new Answer<Stream<FileContent>>() {
              @Override
              public Stream<FileContent> answer(InvocationOnMock invocation) {
                return Stream.of(fileContent);
              }
            })
        .when(item)
        .getContents(Mockito.eq(FileContent.class));

    FileMetadataExtractor extractor = new FileMetadataExtractor();
    ProcessorResponse response = extractor.process(item);

    assertEquals(Status.ITEM_ERROR, response.getStatus());
  }
}
