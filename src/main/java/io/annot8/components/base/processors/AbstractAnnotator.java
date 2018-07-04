package io.annot8.components.base.processors;

import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.ProcessingException;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import io.annot8.defaultimpl.data.SimpleCapabilities.Builder;

public abstract class AbstractAnnotator implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) throws ProcessingException {
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

  @Override
  public Capabilities getCapabilities() {
    // TODO: move simplecapabilties to core as defaultcapabiltiesi. Remove out dependency of default-impl.
    // TODO: add builder to the capabaility interface?

    Builder builder = new SimpleCapabilities.Builder();
    buildCapabilities(builder);
    return builder.save();
  }

  protected abstract void buildCapabilities(SimpleCapabilities.Builder builder);
}
