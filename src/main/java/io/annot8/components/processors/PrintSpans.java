package io.annot8.components.processors;


import io.annot8.common.bounds.SpanBounds;
import io.annot8.common.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import java.util.Optional;

public class PrintSpans extends AbstractTextAnnotator {


  @Override
  protected void process(final Item item, final Text content) throws Annot8Exception {

    content.getAnnotations().getByBounds(SpanBounds.class)
        .forEach(a -> {
          final SpanBounds bounds = a.getBounds(SpanBounds.class).get();
          final Optional<String> value = content.getText(a);

          System.out.println(
              String.format("Annotation %s: %s", bounds.toString(), value.orElse("UNKNOWN"))
          );

        });
  }


}
