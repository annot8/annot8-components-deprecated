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

public class HashTag extends AbstractTextAnnotator {

  private static final Pattern HASHTAG = Pattern.compile("#[a-z0-9]+", Pattern.CASE_INSENSITIVE);

  private static final String TYPE = "hashtag";

  @Override
  protected void process(final Item item, final Text content) throws Annot8Exception {
    AnnotationStore annotationStore = content.getAnnotations();

    final Matcher matcher = HASHTAG.matcher(content.getData());
    while (matcher.find()) {
      annotationStore.create()
          .withBounds(new SpanBounds(matcher.start(), matcher.end()))
          .withType(TYPE)
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
