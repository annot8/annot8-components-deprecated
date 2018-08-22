package io.annot8.components.cyber.processors;

import io.annot8.components.base.processors.AbstractRegex;
import io.annot8.conventions.AnnotationTypes;
import java.util.regex.Pattern;

public class Url extends AbstractRegex {

  public Url() {
    super(
        Pattern.compile("\\b((https?|ftp)://|www.)(([-a-z0-9]+)\\.)?([-a-z0-9.]+\\.[a-z0-9]+)(:([1-9][0-9]{1,5}))?(/([-a-z0-9+&@#/%=~_|$!:,.]*\\?[-a-z0-9+&@#/%=~_|$!:,.]*)|/([-a-z0-9+&@#/%=~_|$!:,.]*[-a-z0-9+&@#/%=~_|$!:,])|/)?", Pattern.CASE_INSENSITIVE),
        0,
        AnnotationTypes.ANNOTATION_TYPE_URL
    );
  }
}
