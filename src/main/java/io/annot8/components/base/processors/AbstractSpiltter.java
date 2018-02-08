package io.annot8.components.base.processors;

import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.ProcessingException;
import java.util.stream.Stream;

public abstract class AbstractSpiltter implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) throws ProcessingException {
    try {
      Stream<Item> split;
      if (acceptsItem(item)) {
        split = splitItem(item);
      } else {
        // If we don't accept the item assume we aren't discarding it...
        split = Stream.empty();
      }

      if (contineProcessing(item)) {
        return ProcessorResponse.ok(split);
      } else {
        return ProcessorResponse.itemStop(split);
      }

    } catch (final Exception e) {
      return ProcessorResponse.itemError();
    }
  }

  protected boolean contineProcessing(final Item item) {
    // by default assuming that if an item is accepted its probably going to be divided
    return false;
  }

  protected boolean acceptsItem(final Item item) {
    return false;
  }

  protected abstract Stream<Item> splitItem(final Item item);

}
