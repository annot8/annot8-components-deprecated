package io.annot8.components.processors.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.annot8.common.data.content.Text;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.test.TestContext;
import io.annot8.test.TestItem;
import io.annot8.test.content.TestStringContent;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class IPv6Test {

  private void doTest(String content, String expectedMatch) throws Annot8Exception {
    Processor p = new IPv6();

    //TODO: These should be replaced by Test* rather than using Simple*
    //TODO: Provide some abstract test base classes that provide this common functionality
//    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
//    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
//    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);
//    Context context = new SimpleContext(itemFactory, EmptySettings.getInstance());

    Item item = new TestItem();
    Context context = new TestContext();

    p.configure(context);

//    Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);
    Text c = item.create(TestStringContent.class).withName("test")
        .withData(content).save();

    p.process(item);

    AnnotationStore store = item.getContent("test").get().getAnnotations();

    List<Annotation> annotations = store.getAll().collect(Collectors.toList());
    assertEquals(1, annotations.size());

    Annotation a = annotations.get(0);
    assertEquals(AnnotationTypes.ANNOTATION_TYPE_IPADDRESS, a.getType());
    assertEquals(c.getName(), a.getContentName());
    assertEquals(expectedMatch, a.getBounds().getData(c).get());
    assertEquals(1, a.getProperties().getAll().size());
    assertEquals(6, a.getProperties().get(PropertyKeys.PROPERTY_KEY_VERSION).get());
  }


  @Test
  public void testFull() throws Exception {
    doTest(
        "Here's a full IPv6 address fe80:0000:0000:0000:0204:61ff:fe9d:f156 and some text after it",
        "fe80:0000:0000:0000:0204:61ff:fe9d:f156");
  }

  @Test
  public void testDropLeadingZeroes() throws Exception {
    doTest("Here's an IPv6 address with leading zeroes dropped: fe80:0:0:0:204:61ff:fe9d:f156.",
        "fe80:0:0:0:204:61ff:fe9d:f156");
  }

  @Test
  public void testCollapseLeadingZeroes() throws Exception {
    doTest("Here's an IPv6 address with collapsed leading zeroes: (fe80::204:61ff:fe9d:f156)",
        "fe80::204:61ff:fe9d:f156");
  }

  @Test
  public void testLocalhost() throws Exception {
    doTest("Here's the localhost IPv6 address: ::1",
        "::1");
  }
}
