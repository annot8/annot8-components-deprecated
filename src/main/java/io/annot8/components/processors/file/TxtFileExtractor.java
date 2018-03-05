package io.annot8.components.processors.file;

import io.annot8.common.content.FileContent;
import io.annot8.common.content.Text;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TxtFileExtractor implements Processor {

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {

    item.getContents(FileContent.class)
        .filter(f -> f.getData().getName().endsWith(".txt"))
        .forEach(f -> {
          try {
            File file = f.getData();
            String data = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            item.create(Text.class)
                .withName("text")
                .withData(data)
                .save();

            // If we processed it ... lets remove it from our item
            // so it doesn't get reprocessed
            item.removeContent(f.getName());

          } catch (Exception e) {
            e.printStackTrace();
          }


        });

    // Always carry on it
    return ProcessorResponse.ok();
  }

  @Override
  public Capabilities getCapabilities() {
    return new SimpleCapabilities.Builder()
        .createsContent(Text.class)
        .requiresContent(FileContent.class)
        .save();
  }
}
