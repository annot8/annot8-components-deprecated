package io.annot8.components.processors.regex;

import io.annot8.common.bounds.SpanBounds;
import io.annot8.common.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.components.base.processors.ContentAnnotatorSettings;
import io.annot8.components.processors.regex.Regex.RegexSettings;
import io.annot8.core.components.Capabilities;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.exceptions.ProcessingException;
import io.annot8.core.settings.SettingsClass;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SettingsClass(RegexSettings.class)
public class Regex extends AbstractTextAnnotator {  //TODO: Are there functions in AbstractTextAnnotator we ought to be implementing?

  protected Pattern regex = null; //TODO: Should we provide a default Pattern to avoid NPEs?
  protected int group = 0;
  protected String type = "";

  public Regex(){
    //Do nothing
  }

  public Regex(Pattern regex, int group, String type){
    this.regex = regex;
    this.group = group;
    this.type = type;
  }

  @Override
  public void configure(Context context) throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    RegexSettings settings = context.getSettings(RegexSettings.class);

    this.regex = settings.getRegex();
    this.group = settings.getGroup();
    this.type = settings.getType();
  }

  @Override
  public Capabilities getCapabilities() {
    return new SimpleCapabilities.Builder()
        .outputsAnnotation(type)
        .save();
  }

  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    if(regex == null)
      throw new BadConfigurationException("Parameter 'regex' must not be null");

    AnnotationStore annotationStore = content.getAnnotations();

    Matcher m = regex.matcher(content.getData());
    while(m.find()){
      try {
        annotationStore.create()
            .withType(type)
            .withBounds(new SpanBounds(m.start(group), m.end(group)))
            .save();
      }catch (IndexOutOfBoundsException e){
        throw new ProcessingException("Invalid group", e);
      }
    }
  }

  public static class RegexSettings extends ContentAnnotatorSettings{
    private final Pattern regex;
    private final int group;
    private final String type;

    public RegexSettings(Pattern regex, int group, String type){
      super(Collections.emptySet(), Collections.emptySet());

      this.regex = regex;
      this.group = group;
      this.type = type;
    }

    public RegexSettings(Pattern regex, int group, String type, Set<String> tags, Set<String> content){
      super(tags, content);

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
