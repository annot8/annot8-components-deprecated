package io.annot8.components.processors.creators;


import io.annot8.common.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.defaultimpl.data.SimpleCapabilities.Builder;

@CreatesContent(Text.class)
public class Capitalise extends AbstractTextAnnotator {

  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    item.create(Text.class)
        .withName("CAPITALISED")
        .withData(content.getData().toUpperCase())
        .save();
  }

}
