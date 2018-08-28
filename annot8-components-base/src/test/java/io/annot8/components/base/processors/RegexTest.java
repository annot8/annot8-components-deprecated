package io.annot8.components.base.processors;

import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.processors.Regex.RegexSettings;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestItem;
import io.annot8.testing.testimpl.content.TestStringContent;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegexTest {

  @Test
  public void testRegex() throws Annot8Exception {
    Processor p = new Regex();

    RegexSettings rs = new RegexSettings(Pattern.compile("[0-9]+"), 0, "number");

    Item item = new TestItem();
    Context context = new TestContext(rs);

    p.configure(context);

    Text content = item.create(TestStringContent.class).withName("test").withData("x + 12 = 42")
        .save();

    p.process(item);

    AnnotationStore store = content.getAnnotations();

    List<Annotation> annotations = store.getAll().collect(Collectors.toList());
    Assertions.assertEquals(2, annotations.size());

    for (Annotation annotation : annotations) {
      Assertions.assertEquals("number", annotation.getType());
      Assertions.assertEquals(content.getId(), annotation.getContentId());
      SpanBounds bounds = annotation.getBounds(SpanBounds.class).get();
      String value = bounds.getData(content).get();
      // Basic impl to handle order not being guaranteed
      switch (value) {
        case "42":
          Assertions.assertEquals(9, bounds.getBegin());
          Assertions.assertEquals(11, bounds.getEnd());
          break;
        case "12":
          Assertions.assertEquals(4, bounds.getBegin());
          Assertions.assertEquals(6, bounds.getEnd());
          break;
        default:
          Assertions.fail("Unexpected value " + value + " detected");
          break;
      }
    }

  }

}
