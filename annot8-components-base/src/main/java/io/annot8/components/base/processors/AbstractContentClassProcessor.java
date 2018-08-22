package io.annot8.components.base.processors;

import io.annot8.core.capabilities.Capabilities;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;

public abstract class AbstractContentClassProcessor<T extends Content<?>>
    extends AbstractContentProcessor {

  private final Class<T> contentClazz;

  protected AbstractContentClassProcessor(final Class<T> contentClazz) {
    this.contentClazz = contentClazz;
  }


  @Override
  protected boolean acceptsContent(final Content<?> content) {
    return contentClazz.isInstance(content);
  }

  @Override
  protected void processContent(final Item item, final Content<?> content) throws Annot8Exception {
    // TODO: We could check the accepts here again before the cast but...
    process(item, (T) content);
  }

  protected abstract void process(final Item item, final T content) throws Annot8Exception;

  @Override
  public void buildCapabilities(Capabilities.Builder builder) {
    super.buildCapabilities(builder);

    builder.processesContent(contentClazz, false);
  }
}
