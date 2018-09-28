package io.annot8.components.tika.processors;

import io.annot8.common.data.content.InputStreamContent;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Content.Builder;
import io.annot8.core.data.Item;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

/**
 * Process {@link InputStreamContent} content objects on the current Item, extracting the text and
 * creating {@link Text} content objects. Metadata is extracted and added as properties.
 */
@ProcessesContent(InputStreamContent.class)
@CreatesContent(Text.class)
public class TikaStreamExtractor extends AbstractComponent implements Processor {

  @Override
  public ProcessorResponse process(Item item) {
    item.getContents(InputStreamContent.class).forEach(c -> createTextFromInputStream(c, item));

    return ProcessorResponse.ok();
  }

  private void createTextFromInputStream(InputStreamContent c, Item item) {
    try {
      BodyContentHandler textHandler = new BodyContentHandler(Integer.MAX_VALUE);
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();

      AutoDetectParser autoParser = new AutoDetectParser();
      autoParser.parse(c.getData(), textHandler, metadata, context);

      Builder<Text, String> builder = item.create(Text.class)
        .withName(c.getName() + "-tika")
        .withData(textHandler.toString());

      for (String name : metadata.names()) {
        builder.withProperty(name, metadata.get(name));
      }

      builder.save();
    } catch (Exception e) {
      log().warn("Error extracting content from InputStream", e);
    }
  }
}
