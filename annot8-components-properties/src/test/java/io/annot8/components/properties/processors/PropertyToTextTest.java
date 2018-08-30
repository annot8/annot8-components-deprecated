package io.annot8.components.properties.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.annot8.common.data.content.Text;
import io.annot8.common.implementations.stores.AnnotationStoreFactory;
import io.annot8.components.monitor.resources.Logging;
import io.annot8.components.properties.processors.PropertyToText.PropertyToTextSettings;
import io.annot8.core.components.Processor;
import io.annot8.core.components.Resource;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.Settings;
import io.annot8.defaultimpl.content.SimpleText;
import io.annot8.defaultimpl.context.SimpleContext;
import io.annot8.defaultimpl.data.SimpleItem;
import io.annot8.defaultimpl.factories.SimpleContentBuilderFactoryRegistry;
import io.annot8.defaultimpl.factories.SimpleItemFactory;
import io.annot8.defaultimpl.stores.SimpleAnnotationStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class PropertyToTextTest {

  @Test
  public void testPropertyToText() throws Annot8Exception {
    AnnotationStoreFactory factory = SimpleAnnotationStore.factory();
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory(factory));

    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);

    Logging logging = Logging.useLoggerFactory();
    Map<String, Resource> resources = new HashMap<>();
    resources.put("logging", logging);

    Context context = new SimpleContext(itemFactory, resources);

    try(Processor p = new PropertyToText()) {

      p.configure(context);

      Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);

      item.getProperties().set("test", "Hello World!");

      assertEquals(0, item.getContents().count());

      p.process(item);

      AtomicInteger count = new AtomicInteger();
      item.getContents().forEach(c -> {
            count.getAndIncrement();
            assertEquals("test", c.getName());
            assertEquals("Hello World!", c.getData());
          }
      );

      assertEquals(1, count.get());

    }
  }

  @Test
  public void testWhitelist() throws Annot8Exception {
    AnnotationStoreFactory factory = SimpleAnnotationStore.factory();
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory(factory));

    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);

    Logging logging = Logging.useLoggerFactory();
    Map<String, Resource> resources = new HashMap<>();
    resources.put("logging", logging);

    Settings settings = new PropertyToTextSettings();
    ((PropertyToTextSettings) settings).setWhitelist(new HashSet<>(Arrays.asList("test")));

    Context context = new SimpleContext(itemFactory, Arrays.asList(settings), resources);

    try(Processor p = new PropertyToText()) {

      p.configure(context);

      Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);

      item.getProperties().set("test", "Hello World!");
      item.getProperties().set("foo", "bar");

      assertEquals(0, item.getContents().count());

      p.process(item);

      AtomicInteger count = new AtomicInteger();
      item.getContents().forEach(c -> {
            count.getAndIncrement();
            assertEquals("test", c.getName());
            assertEquals("Hello World!", c.getData());
          }
      );

      assertEquals(1, count.get());

    }
  }

  @Test
  public void testBlacklist() throws Annot8Exception {
    AnnotationStoreFactory factory = SimpleAnnotationStore.factory();
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory(factory));

    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);

    Logging logging = Logging.useLoggerFactory();
    Map<String, Resource> resources = new HashMap<>();
    resources.put("logging", logging);

    Settings settings = new PropertyToTextSettings();
    ((PropertyToTextSettings) settings).setBlacklist(new HashSet<>(Arrays.asList("foo")));

    Context context = new SimpleContext(itemFactory, Arrays.asList(settings), resources);

    try(Processor p = new PropertyToText()) {

      p.configure(context);

      Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);

      item.getProperties().set("test", "Hello World!");
      item.getProperties().set("foo", "bar");

      assertEquals(0, item.getContents().count());

      p.process(item);

      AtomicInteger count = new AtomicInteger();
      item.getContents().forEach(c -> {
            count.getAndIncrement();
            assertEquals("test", c.getName());
            assertEquals("Hello World!", c.getData());
          }
      );

      assertEquals(1, count.get());

    }
  }
}
