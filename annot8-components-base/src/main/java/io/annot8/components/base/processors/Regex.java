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
import io.annot8.core.settings.SettingsClass;
import io.annot8.core.stores.AnnotationStore;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for regex annotators
 */
@SettingsClass(RegexSettings.class)
public class Regex extends
    AbstractRegex{  //TODO: Are there functions in AbstractTextProcessor we ought to be implementing?

  public Regex() {
    //Do nothing
  }

  public Regex(Pattern pattern, int group, String type) {
    this.pattern = pattern;
    this.group = group;
    this.type = type;
  }

  @Override
  public void configure(Context context)
      throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    RegexSettings settings = context.getSettings(RegexSettings.class);

    this.pattern = settings.getRegex();
    this.group = settings.getGroup();
    this.type = settings.getType();
  }


  public static class RegexSettings extends ContentAnnotatorSettings {

    private final Pattern regex;
    private final int group;
    private final String type;

    public RegexSettings(Pattern regex, int group, String type) {
      super(Collections.emptySet());

      this.regex = regex;
      this.group = group;
      this.type = type;
    }

    public RegexSettings(Pattern regex, int group, String type, Set<String> content) {
      super(content);

      this.regex = regex;
      this.group = group;
      this.type = type;
    }

    public Pattern getRegex() {
      return regex;
    }

    public int getGroup() {
      return group;
    }

    public String getType() {
      return type;
    }
  }
}
