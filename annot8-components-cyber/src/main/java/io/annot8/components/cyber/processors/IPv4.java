package io.annot8.components.cyber.processors;

import io.annot8.components.base.processors.AbstractSuppliedRegex;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation.Builder;
import java.util.regex.Pattern;

public class IPv4 extends AbstractSuppliedRegex {

  public IPv4() {
    super(
        Pattern.compile(
            "\\b(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b"),
        0,
        AnnotationTypes.ANNOTATION_TYPE_IPADDRESS
    );
  }

  @Override
  protected void addProperties(Builder builder) {
    super.addProperties(builder);
    builder.withProperty(PropertyKeys.PROPERTY_KEY_VERSION, 4);
  }

}
