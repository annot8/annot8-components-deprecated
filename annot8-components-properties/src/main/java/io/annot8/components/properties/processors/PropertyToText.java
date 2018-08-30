package io.annot8.components.properties.processors;

import io.annot8.common.data.content.Text;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;

/**
 * Convert properties on an item to separate Text content so they can be processed.
 * The toString() function is used to convert properties into a String.
 */
@CreatesContent(Text.class)
public class PropertyToText extends AbstractComponent implements Processor {

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {
    item.getProperties().getAll().forEach((k, v) -> {
      try {
        item.create(Text.class)
          .withName(k)
          .withData(v.toString())
          .save();
      }catch (UnsupportedContentException | IncompleteException e){
        log().error("Unable to create Text content", e);
      }
    });

    return ProcessorResponse.ok();
  }
}
