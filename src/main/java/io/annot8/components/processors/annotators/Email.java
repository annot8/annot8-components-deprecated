package io.annot8.components.processors.annotators;

import io.annot8.common.bounds.SpanBounds;
import io.annot8.common.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.Settings;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.defaultimpl.data.SimpleCapabilities.Builder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email extends AbstractTextAnnotator {

  private static final Pattern EMAIL =
      Pattern.compile("[A-Z0-9._%+-]+@([A-Z0-9.-]+[.][A-Z]{2,6})", Pattern.CASE_INSENSITIVE);


  private static final String TYPE = "email";

  @Override
  protected void process(final Item item, final Text content) throws Annot8Exception {

    AnnotationStore annotationStore = content.getAnnotations();

    final Matcher matcher = EMAIL.matcher(content.getData());
    while (matcher.find()) {

      annotationStore.create()
          .withType(TYPE)
          .withBounds(new SpanBounds(matcher.start(), matcher.end()))
          .save();

    }
  }

  @Override
  protected void buildCapabilities(Settings settings, Builder builder) {
    super.buildCapabilities(settings, builder);
    builder.outputsBounds(SpanBounds.class);
    builder.outputsAnnotation(TYPE);
  }
}
