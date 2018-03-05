package io.annot8.components.processors.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.annot8.common.content.Text;
import io.annot8.common.factories.ItemFactory;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.defaultimpl.content.SimpleText;
import io.annot8.defaultimpl.context.SimpleContext;
import io.annot8.defaultimpl.data.SimpleItem;
import io.annot8.defaultimpl.factories.SimpleContentBuilderFactoryRegistry;
import io.annot8.defaultimpl.factories.SimpleItemFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class IPv4Test {

  @Test
  public void testIPv4() throws Annot8Exception {
    Processor p = new IPv4();

    //TODO: These should be replaced by Test* rather than using Simple*
    //TODO: Provide some abstract test base classes that provide this common functionality
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);
    Context context = new SimpleContext(itemFactory, EmptySettings.getInstance());

    p.configure(context);

    Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);
    Text content = item.create(Text.class).withName("test")
        .withData("The attack came from 127.0.0.1").save();

    p.process(item);

    AnnotationStore store = item.getContent("test").get().getAnnotations();

    List<Annotation> annotations = store.getAll().collect(Collectors.toList());
    assertEquals(1, annotations.size());

    Annotation a = annotations.get(0);
    assertEquals(AnnotationTypes.ANNOTATION_TYPE_IPADDRESS, a.getType());
    assertEquals(content.getName(), a.getContentName());
    assertEquals("127.0.0.1", a.getBounds().getData(content).get());
    assertEquals(1, a.getProperties().getAll().size());
    assertEquals(4, a.getProperties().get(PropertyKeys.PROPERTY_KEY_VERSION).get());
  }
}