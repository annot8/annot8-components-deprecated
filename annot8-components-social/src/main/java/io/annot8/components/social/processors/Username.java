package io.annot8.components.social.processors;

import io.annot8.components.base.processors.AbstractRegex;
import io.annot8.conventions.AnnotationTypes;
import java.util.regex.Pattern;

public class Username extends AbstractRegex {

  public Username() {
    super(
        Pattern.compile("\\B@[-_a-z0-9]+\\b", Pattern.CASE_INSENSITIVE),
        0,
        AnnotationTypes.ANNOTATION_TYPE_USERNAME
    );
  }
}
