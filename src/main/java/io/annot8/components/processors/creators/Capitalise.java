package io.annot8.components.processors.creators;


import io.annot8.common.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.Settings;
import io.annot8.defaultimpl.data.SimpleCapabilities.Builder;

public class Capitalise extends AbstractTextAnnotator {

  @Override
  protected void process(Item item, Text content) throws Annot8Exception {
    item.create(Text.class)
        .withName("CAPITALISED")
        .withData(content.getData().toUpperCase())
        .save();
  }

  @Override
  protected void buildCapabilities(Settings settings, Builder builder) {
    super.buildCapabilities(settings, builder);
    builder.createsContent(Text.class);
  }
}
