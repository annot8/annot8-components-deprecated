package io.annot8.components.base.processors;

import io.annot8.common.data.content.Text;

public abstract class AbstractTextProcessor extends AbstractContentClassProcessor<Text> {

  protected AbstractTextProcessor() {
    super(Text.class);
  }

}
