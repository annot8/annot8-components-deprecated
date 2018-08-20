package io.annot8.components.processors.regex;

import io.annot8.conventions.AnnotationTypes;
import java.util.regex.Pattern;

public class HashTag extends AbstractSuppliedRegex {
  public HashTag(){
    super(
      Pattern.compile("#[a-z0-9]+", Pattern.CASE_INSENSITIVE),
      0,
      AnnotationTypes.ANNOTATION_TYPE_HASHTAG
    );
  }
}
