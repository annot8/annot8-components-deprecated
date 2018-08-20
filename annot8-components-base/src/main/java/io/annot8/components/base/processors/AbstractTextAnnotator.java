package io.annot8.components.base.processors;

import io.annot8.common.data.content.Text;

public abstract class AbstractTextAnnotator extends AbstractContentClassAnnotator<Text> {

  protected AbstractTextAnnotator() {
    super(Text.class);
  }

}
