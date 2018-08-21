package io.annot8.components.cyber.processors;

import io.annot8.components.base.processors.AbstractSuppliedRegex;
import io.annot8.conventions.AnnotationTypes;
import java.util.regex.Pattern;

public class Email extends AbstractSuppliedRegex {

  public Email() {
    super(
        Pattern.compile("[A-Z0-9._%+-]+@([A-Z0-9.-]+[.][A-Z]{2,6})", Pattern.CASE_INSENSITIVE),
        0,
        AnnotationTypes.ANNOTATION_TYPE_EMAIL
    );
  }
}
