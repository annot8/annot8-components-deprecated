package io.annot8.components.base.processors;

import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;

public abstract class AbstractAnnotator extends AbstractComponent implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) {
    try {
      if (acceptsItem(item)) {
        final boolean stop = processItem(item);
        if (!stop) {
          item.discard();
        }
      }
      return ProcessorResponse.ok();
    } catch (final Exception e) {
      return ProcessorResponse.itemError();
    }
  }

  protected boolean acceptsItem(final Item item) {
    return true;
  }

  protected abstract boolean processItem(final Item item);

}