package io.annot8.components.quantities.processors;

import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.processors.AbstractTextProcessor;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation.Builder;
import io.annot8.core.capabilities.CreatesAnnotation;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CreatesAnnotation(value = AnnotationTypes.ANNOTATION_TYPE_AREA, bounds = SpanBounds.class)
public class Area extends AbstractTextProcessor {

  private static final double MM2_TO_M2 = 0.000001;
  private static final double CM2_TO_M2 = 0.0001;
  private static final double KM2_TO_M2 = 1000000.0;

  private static final double MI2_TO_M2 = 2589988.1;
  private static final double YD2_TO_M2 = 0.83612739;
  private static final double FT2_TO_M2 = 0.092903044;
  private static final double IN2_TO_M2 = 0.00064516;

  private static final double ACRE_TO_M2 = 4046.8564;
  private static final double HECTARE_TO_M2 = 10000.0;

  private final Pattern m2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(m\\^2|square metre|square meter|square m)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern mm2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(mm\\^2|square millimetre|square millimeter|square mm)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern cm2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(cm\\^2|square centimetre|square centimeter|square cm)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern km2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(km\\^2|square kilometre|square kilometers|square km)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern mi2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(mi\\^2|square miles|square mi)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern yd2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(yd\\^2|square yard|square yd)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern ft2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(ft\\^2|square foot|square feet|square ft)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern in2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(in\\^2|square inch|square in|square inche)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern haPattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(hectare|ha)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern acrePattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(acre)(s)?\\b",
          Pattern.CASE_INSENSITIVE);


  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    process(content, mm2Pattern, MM2_TO_M2);
    process(content, cm2Pattern, CM2_TO_M2);
    process(content, m2Pattern, 1.0);
    process(content, km2Pattern, KM2_TO_M2);

    process(content, in2Pattern, IN2_TO_M2);
    process(content, ft2Pattern, FT2_TO_M2);
    process(content, yd2Pattern, YD2_TO_M2);
    process(content, mi2Pattern, MI2_TO_M2);

    process(content, acrePattern, ACRE_TO_M2);
    process(content, haPattern, HECTARE_TO_M2);
  }

  private void process(Text content, Pattern pattern, double normalization) throws Annot8Exception{
    Matcher m = pattern.matcher(content.getData());
    while(m.find()){
      Builder builder = content.getAnnotations().create()
          .withType(AnnotationTypes.ANNOTATION_TYPE_AREA)
          .withBounds(new SpanBounds(m.start(), m.end()));

      try {
        builder = builder
          .withProperty(PropertyKeys.PROPERTY_KEY_VALUE,
            normalise(m.group(1), m.group(2), normalization))
          .withProperty(PropertyKeys.PROPERTY_KEY_UNIT, "m^2");
      }catch (Exception e){
        log().warn("Unable to parse and normalise value", e);
      }

      builder.save();
    }
  }

  private double normalise(String number, String multiplier, double normalization){
    double n = Double.parseDouble(number.replaceAll("[^0-9\\.]", ""));

    long m = 1L;
    if(multiplier != null) {
      switch (multiplier.toLowerCase()) {
        case "thousand":
          m = 1000L;
          break;
        case "million":
          m = 1000000L;
          break;
        case "billion":
          m = 1000000000L;
          break;
        case "trillion":
          m = 1000000000000L;
          break;
      }
    }

    return n*m*normalization;
  }
}
