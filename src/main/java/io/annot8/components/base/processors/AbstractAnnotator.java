package io.annot8.components.base.processors;

import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.ProcessingException;
import io.annot8.core.settings.Settings;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import io.annot8.defaultimpl.data.SimpleCapabilities.Builder;

public abstract class AbstractAnnotator implements Processor {

  @Override
  public final ProcessorResponse process(final Item item) throws ProcessingException {
    try {
      if (acceptsItem(item)) {
        final boolean stop = processItem(item);
        if (!stop) {
          // Don't allow to continue
          return ProcessorResponse.itemStop();
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
  // TODO: Settings class here seems a bit fishy?
  public Capabilities getCapabilities(Settings settings) {
    // TODO: move simplecapabilties to core as defaultcapabiltiesi. Remove out dependency of default-impl.
    // TODO: add builder to the capabaility interface?

    Builder builder = new SimpleCapabilities.Builder();
    buildCapabilities(settings, builder);
    return builder.save();
  }

  protected abstract void buildCapabilities(Settings settings, SimpleCapabilities.Builder builder);
}
