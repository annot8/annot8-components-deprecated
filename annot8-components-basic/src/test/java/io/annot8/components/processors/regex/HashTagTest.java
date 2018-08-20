package io.annot8.components.processors.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.annot8.common.data.content.Text;
import io.annot8.core.data.ItemFactory;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.stores.AnnotationStore;

import io.annot8.test.TestContext;
import io.annot8.test.TestItem;
import io.annot8.test.content.TestStringContent;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class HashTagTest {
  @Test
  public void testEmail() throws Annot8Exception {
    Processor p = new HashTag();

    //TODO: These should be replaced by Test* rather than using Simple*
    //TODO: Provide some abstract test base classes that provide this common functionality
//    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
//    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
//    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);
//    Context context = new SimpleContext(itemFactory, EmptySettings.getInstance());

    Item item = new TestItem();
    Context context = new TestContext();

    p.configure(context);

    Text content = item.create(TestStringContent.class).withName("test")
        .withData("Prime Minister making a speech #latestnews").save();

    p.process(item);

    AnnotationStore store = item.getContent("test").get().getAnnotations();

    List<Annotation> annotations = store.getAll().collect(Collectors.toList());
    assertEquals(1, annotations.size());

    Annotation a = annotations.get(0);
    assertEquals(AnnotationTypes.ANNOTATION_TYPE_HASHTAG, a.getType());
    assertEquals(content.getName(), a.getContentName());
    assertEquals("#latestnews", a.getBounds().getData(content).get());
    assertEquals(0, a.getProperties().getAll().size());
  }
}
