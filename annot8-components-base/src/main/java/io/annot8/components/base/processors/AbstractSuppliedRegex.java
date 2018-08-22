package io.annot8.components.base.processors;


import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.core.capabilities.Capabilities.Builder;
import io.annot8.core.context.Context;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.settings.SettingsClass;
import java.util.regex.Pattern;

// TODO: CF - I think this and the Regex are wrong way around in parent child class heirarchy (Regex should not have settings)
/**
 * Base class for regex annotators
 */
@SettingsClass(EmptySettings.class)
public abstract class AbstractSuppliedRegex extends Regex {

  public AbstractSuppliedRegex(Pattern regex, int group, String type) {
    super(regex, group, type);
  }

  @Override
  public void configure(Context context)
      throws BadConfigurationException, MissingResourceException {
    super.configure(context);
  }


}
