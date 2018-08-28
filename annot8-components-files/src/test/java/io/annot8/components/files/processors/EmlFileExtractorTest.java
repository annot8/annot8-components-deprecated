package io.annot8.components.files.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.annot8.common.data.content.FileContent;
import io.annot8.common.data.content.Text;
import io.annot8.components.monitor.resources.Logging;
import io.annot8.core.components.Processor;
import io.annot8.core.components.Resource;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.defaultimpl.content.SimpleFile;
import io.annot8.defaultimpl.content.SimpleText;
import io.annot8.defaultimpl.context.SimpleContext;
import io.annot8.defaultimpl.data.SimpleItem;
import io.annot8.defaultimpl.factories.SimpleContentBuilderFactoryRegistry;
import io.annot8.defaultimpl.factories.SimpleItemFactory;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class EmlFileExtractorTest {

  @Test
  public void test() throws Exception{



    try(
      Processor p = new EmlFileExtractor()
    ) {
      SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
      contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
      //FIXME: https://github.com/annot8/annot8-default-impl/issues/4
      //contentBuilderFactoryRegistry.register(InputStreamContent.class, new SimpleInputStream.BuilderFactory());
      contentBuilderFactoryRegistry.register(FileContent.class, new SimpleFile.BuilderFactory());

      ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);

      Logging logging = Logging.useLoggerFactory();
      Map<String, Resource> resources = new HashMap<>();
      resources.put("logging", logging);

      Context context = new SimpleContext(itemFactory, resources);
      p.configure(context);

      Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);

      URL resource = EmlFileExtractorTest.class.getResource("test_sample_message.eml");   //Based on https://www.phpclasses.org/browse/file/14672.html
      File f = Paths.get(resource.toURI()).toFile();

      item.create(FileContent.class)
          .withName("test_sample_message.eml")
          .withData(f)
          .save();

      p.process(item);

      assertEquals("mlemos <mlemos@acm.org>", item.getProperties().get("From").get());
      assertEquals("Manuel Lemos <mlemos@linux.local>", item.getProperties().get("To").get());
      assertEquals("http://www.phpclasses.org/mimemessage $Revision: 1.63 $ (mail)", item.getProperties().get("X-Mailer").get());
      assertEquals("Sat, 30 Apr 2005 19:28:29 -0300", item.getProperties().get("Date").get());
      assertEquals(Arrays.asList("Original file from https://www.phpclasses.org/browse/file/14672.html", "Modified by James Baker"), item.getProperties().get("Comment").get());

      Text text1 = (Text) item.getContent("body-1-1-1").get();
      assertNotNull(text1);
      assertEquals("text/plain; charset=ISO-8859-1", text1.getProperties().get("Content-Type").get());
      assertTrue(text1.getData().contains("Please use an HTML capable mail program"));

      Text text2 = (Text) item.getContent("body-1-1-2").get();
      assertNotNull(text2);
      assertEquals("text/html; charset=ISO-8859-1", text2.getProperties().get("Content-Type").get());
      assertTrue(text2.getData().contains("Testing Manuel Lemos' MIME E-mail composing and sending PHP class: HTML message"));

      //TODO: Test attachments
    }
  }
}
