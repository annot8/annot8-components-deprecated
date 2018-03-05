package io.annot8.components.processors.file;

import io.annot8.common.content.FileContent;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.defaultimpl.data.SimpleCapabilities;

// This is more like premature optimisation... really but I guess it's sensible to clean up
// the pipeline as we go.
public class DiscardUnextractedFiles implements Processor {

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {

    item.getContents(FileContent.class)
        .map(Content::getName)
        .forEach(item::removeContent);

    boolean noOtherContent = item.getContents().count() == 0;

    if (noOtherContent) {
      return ProcessorResponse.itemStop();
    } else {
      return ProcessorResponse.ok();
    }
  }

  @Override
  public Capabilities getCapabilities() {
    return new SimpleCapabilities.Builder()
        .requiresContent(FileContent.class)
        .save();
  }
}
