package io.annot8.components.base.processors;

import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.ProcessingException;

public abstract class AbstractSplitter implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) throws ProcessingException {
    try {
      if (acceptsItem(item)) {
        boolean discard = split(item);

        if (discard) {
          item.discard();
        }
      }


      return ProcessorResponse.ok();

    } catch (final Exception e) {
      return ProcessorResponse.itemError();
    }
  }

  protected boolean acceptsItem(final Item item) {
    return false;
  }

  protected abstract boolean split(final Item item);

}
