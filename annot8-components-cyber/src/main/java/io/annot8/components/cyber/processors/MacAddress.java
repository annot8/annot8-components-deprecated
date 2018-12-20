package io.annot8.components.cyber.processors;

import com.google.common.io.BaseEncoding;
import io.annot8.components.base.processors.AbstractRegex;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation.Builder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract MAC Addresses (EUI-48) in common formats from text
 */
public class MacAddress extends AbstractRegex {

  public MacAddress() {
    super(
        Pattern.compile("(([0-9A-F]{2}[-:]){5}[0-9A-F]{2})|(([0-9A-F]{4}\\.){2}[0-9A-F]{4})", Pattern.CASE_INSENSITIVE),
        0,
        AnnotationTypes.ANNOTATION_TYPE_MACADDRESS);
  }

  @Override
  protected void addProperties(Builder builder, Matcher m) {
    String norm = m.group(0).toUpperCase().replaceAll("[^0-9A-F]", "");
    builder.withProperty(PropertyKeys.PROPERTY_KEY_VALUE, BaseEncoding.base16().decode(norm));
  }
}
