package io.annot8.components.base.processors;

import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;

public abstract class AbstractFilter implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) {
    try {
      if (filter(item)) {
        item.discard();
      }

      return ProcessorResponse.ok();
    } catch (final Exception e) {
      return ProcessorResponse.itemError();
    }
  }

  protected abstract boolean filter(final Item item);

}
