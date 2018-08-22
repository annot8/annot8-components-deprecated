package io.annot8.components.base.processors;

import io.annot8.core.context.Context;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.settings.SettingsClass;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A base class for content processors which limit to content by (configurable name)
 */
@SettingsClass(ContentAnnotatorSettings.class)
public abstract class AbstractContentProcessor extends AbstractItemProcessor {

  private ContentAnnotatorSettings settings;

  @Override
  public void configure(final Context context)
      throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    settings = context.getSettings(ContentAnnotatorSettings.class);
  }

  @Override
  protected final boolean processItem(final Item item) {

    Stream<Content<?>> contentToProcess;
    // Did we limit the views?
    if (settings == null || settings.getContent() == null || settings.getContent().isEmpty()) {
      contentToProcess = item.getContents();
    } else {
      contentToProcess = settings.getContent().stream().map(item::getContent)
          .filter(Optional::isPresent).map(Optional::get);
    }

    contentToProcess
        .filter(this::acceptsContent)
        .forEach(c -> {
          try {
            metrics().counter("content.accepted").increment();
            processContent(item, c);
          } catch (Annot8Exception e) {
            metrics().counter("content.errors").increment();
            log().warn("Unable to process content {}", c.getName(), e);
          }
        });

    // Always pass on to next
    return true;
  }

  /**
   *
   * @param content
   * @return
   */
  protected boolean acceptsContent(final Content<?> content) {
    return true;
  }

  protected abstract void processContent(final Item item, final Content<?> content)
      throws Annot8Exception;

}
