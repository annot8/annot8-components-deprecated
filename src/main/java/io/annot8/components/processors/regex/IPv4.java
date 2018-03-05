package io.annot8.components.processors.regex;

import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.properties.MutableProperties;
import io.annot8.core.properties.Properties;
import io.annot8.defaultimpl.properties.SimpleMutableProperties;
import java.util.regex.Pattern;

public class IPv4 extends AbstractSuppliedRegex {
  public IPv4(){
    super(
      Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b"),
      0,
      AnnotationTypes.ANNOTATION_TYPE_IPADDRESS
    );
  }

  @Override
  protected Properties getProperties() {
    MutableProperties properties = new SimpleMutableProperties();
    properties.set(PropertyKeys.PROPERTY_KEY_VERSION, 4);

    return properties;
  }
}
