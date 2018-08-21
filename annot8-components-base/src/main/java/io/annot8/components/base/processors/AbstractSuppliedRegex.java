package io.annot8.components.base.processors;


import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.core.capabilities.Capabilities.Builder;
import io.annot8.core.context.Context;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.settings.SettingsClass;
import java.util.regex.Pattern;

@SettingsClass(EmptySettings.class)
public abstract class AbstractSuppliedRegex extends Regex {

  public AbstractSuppliedRegex(Pattern regex, int group, String type) {
    super(regex, group, type);
  }

  @Override
  public void configure(Context context) {
    // Nothing to configure
  }

  @Override
  public void buildCapabilities(Builder builder) {
    super.buildCapabilities(builder);

    builder.createsAnnotation(type, SpanBounds.class);
  }

}
