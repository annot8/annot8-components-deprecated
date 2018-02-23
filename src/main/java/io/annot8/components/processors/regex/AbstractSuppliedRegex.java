package io.annot8.components.processors.regex;

import io.annot8.core.context.Context;
import io.annot8.core.settings.DefaultSettings;
import io.annot8.core.settings.SettingsClass;
import java.util.regex.Pattern;

@SettingsClass(DefaultSettings.class) //TODO: Check that this works, and that it's not still requesting RegexSettings
public abstract class AbstractSuppliedRegex extends Regex {

  public AbstractSuppliedRegex(Pattern regex, int group, String type){
    super(regex, group, type);
  }

  @Override
  public void configure(Context context) {
    // Nothing to configure
  }
}
