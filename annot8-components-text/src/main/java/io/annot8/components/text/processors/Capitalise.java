package io.annot8.components.text.processors;


import io.annot8.common.data.content.Text;
import io.annot8.components.base.processors.AbstractTextProcessor;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;

@CreatesContent(Text.class)
public class Capitalise extends AbstractTextProcessor {

  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    item.create(Text.class)
        .withName(content.getName() + "_capitalised")
        .withData(content.getData().toUpperCase())
        .save();
  }

}
