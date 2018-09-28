package io.annot8.components.files.processors;

import io.annot8.common.data.content.FileContent;
import io.annot8.common.data.content.InputStreamContent;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.UnsupportedContentException;
import java.io.IOException;
import java.nio.file.Files;

@ProcessesContent(FileContent.class)
@CreatesContent(InputStreamContent.class)
public class FileToInputStream extends AbstractComponent implements Processor {

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {
    item.getContents(FileContent.class).forEach(f -> {
      try {
        item.create(InputStreamContent.class)
            .withName(f.getName() + "-inputstream")
            .withData(Files.newInputStream(f.getData().toPath()));
      }catch (UnsupportedContentException | IOException e){
        log().error("Unable to convert File content to InputStream", e);
      }
    });

    return ProcessorResponse.ok();
  }
}
