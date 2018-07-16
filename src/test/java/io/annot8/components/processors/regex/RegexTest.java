package io.annot8.components.processors.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import io.annot8.common.bounds.SpanBounds;
import io.annot8.common.content.Text;
import io.annot8.components.processors.regex.Regex.RegexSettings;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.defaultimpl.content.SimpleText;
import io.annot8.defaultimpl.context.SimpleContext;
import io.annot8.defaultimpl.data.SimpleItem;
import io.annot8.defaultimpl.factories.SimpleContentBuilderFactoryRegistry;
import io.annot8.defaultimpl.factories.SimpleItemFactory;

public class RegexTest {

  @Test
  public void testRegex() throws Annot8Exception {
    Processor p = new Regex();

    RegexSettings rs = new RegexSettings(Pattern.compile("[0-9]+"), 0, "number");

    // TODO: These should be replaced by Test* rather than using Simple*
    // TODO: Provide some abstract test base classes that provide this common functionality
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry =
        new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry, a -> {
    });
    Context context = new SimpleContext(itemFactory, rs);

    p.configure(context);

    Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);
    Text content = item.create(Text.class).withName("test").withData("x + 12 = 42").save();

    p.process(item);

    AnnotationStore store = item.getContent("test").get().getAnnotations();

    List<Annotation> annotations = store.getAll().collect(Collectors.toList());
    assertEquals(2, annotations.size());

    for (Annotation annotation : annotations) {
      assertEquals("number", annotation.getType());
      assertEquals(content.getName(), annotation.getContentName());
      SpanBounds bounds = annotation.getBounds(SpanBounds.class).get();
      String value = bounds.getData(content).get();
      // Basic impl to handle order not being guaranteed
      if (value.equals("42")) {
        assertEquals(9, bounds.getBegin());
        assertEquals(11, bounds.getEnd());
      } else if (value.equals("12")) {
        assertEquals(4, bounds.getBegin());
        assertEquals(6, bounds.getEnd());
      } else {
        fail("Unexpected value " + value + " detected");
      }
    }

  }

}
