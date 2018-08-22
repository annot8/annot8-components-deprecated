package io.annot8.components.base.processors;


import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.processors.Regex.RegexSettings;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.capabilities.Capabilities.Builder;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.exceptions.ProcessingException;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.settings.SettingsClass;
import io.annot8.core.stores.AnnotationStore;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for regex annotators
 */
public class AbstractRegex extends
    AbstractTextProcessor {

  protected Pattern pattern = null; //TODO: Should we provide a default Pattern to avoid NPEs?
  protected int group = 0;
  protected String type = "";

  public AbstractRegex() {
    //Do nothing
  }

  public AbstractRegex(Pattern pattern, int group, String type) {
    this.pattern = pattern;
    this.group = group;
    this.type = type;
  }

  @Override
  public void buildCapabilities(Builder builder) {
    super.buildCapabilities(builder);

    builder.createsAnnotation(type, SpanBounds.class);
  }


  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    if (pattern == null) {
      throw new BadConfigurationException("Parameter 'pattern' must not be null");
    }

    AnnotationStore annotationStore = content.getAnnotations();

    Matcher m = pattern.matcher(content.getData());
    while (m.find()) {
      if (!acceptMatch(m)) {
        continue;
      }

      try {

        Annotation.Builder builder = annotationStore.create();
        addProperties(builder);

        builder
            .withType(type)
            .withBounds(new SpanBounds(m.start(group), m.end(group)))
            .save();
      } catch (IndexOutOfBoundsException e) {
        throw new ProcessingException("Invalid group", e);
      }
    }
  }

  protected void addProperties(Annotation.Builder builder) {
    // Do nothing
  }

  protected boolean acceptMatch(final Matcher m) {
    return true;
  }

}