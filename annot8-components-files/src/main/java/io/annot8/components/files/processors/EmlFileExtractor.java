package io.annot8.components.files.processors;


import com.google.common.io.CharStreams;
import io.annot8.common.data.content.FileContent;
import io.annot8.common.data.content.InputStreamContent;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;

@ProcessesContent(FileContent.class)
@CreatesContent(Text.class)
@CreatesContent(InputStreamContent.class)
public class EmlFileExtractor extends AbstractComponent implements Processor {

  @Override
  public ProcessorResponse process(Item item) {

    item.getContents(FileContent.class)
        .filter(f -> f.getData().getName().endsWith(".eml") || f.getData().getName().endsWith(".msg"))
        .forEach(f -> {
          try {
            Message message = Message.Builder.of(new FileInputStream(f.getData())).build();

            message.getHeader().getFields().forEach(field -> item.getProperties().set(field.getName(), field.getBody()));

            Body body = message.getBody();
            if (body instanceof SingleBody) {
              // Single body part, so create a new content
              createContentFromBody(item, body, "body");
            } else if (body instanceof Multipart) {
              // Multi body part - attachments should become children items, other bodies become new content
              int bodyCount = 0;
              Multipart multipart = (Multipart) body;
              for (Entity entity : multipart.getBodyParts()) {
                if (entity.getFilename() != null) {
                  // Attachment
                  createItemFromBody(item, entity.getBody(), entity.getFilename());
                } else {
                  // Message
                  bodyCount++;
                  createContentFromBody(item, body, "body-" + bodyCount);
                }
              }
            } else {
              log().warn("Unexpected body type {}", body.getClass().getName());
            }

            //Remove the original *.eml content to avoid reprocessing
            item.removeContent(f.getName());
          }catch (IOException e){
            log().error("Could not read file {} in content {}", f.getData().getName(), f.getName(), e);
          }
        });

    // Always carry on
    return ProcessorResponse.ok();
  }

  private void createContentFromBody(Item item, Body body, String name) {
    try {
      if (body instanceof TextBody) {
        TextBody textBody = (TextBody) body;
        String text = CharStreams.toString(textBody.getReader());

        item.create(Text.class)
            .withData(text)
            .withName(name)
            .withProperty(PropertyKeys.PROPERTY_KEY_CHARSET, textBody.getMimeCharset())
            .save();
      } else if (body instanceof BinaryBody) {
        BinaryBody binaryBody = (BinaryBody) body;

        item.create(InputStreamContent.class)
            .withData(binaryBody.getInputStream())
            .withName(name)
            .save();
      } else {
        log().warn("Unexpected body type {}", body.getClass().getName());
      }


    }catch (Exception e){
      log().error("Unable to create content from body", e);
    }
  }

  private void createItemFromBody(Item item, Body body, String name) {
    try {
      InputStream inputStream;
      if (body instanceof SingleBody) {
        inputStream = ((SingleBody) body).getInputStream();
      }else{
        log().warn("Unexpected body type {}", body.getClass().getName());
        return;
      }

      item.createChildItem()
      .create(InputStreamContent.class)
        .withData(inputStream)
        .withName(name)
        .save();
    }catch (Exception e){
      log().error("Unable to create new item from body", e);
    }
  }
}
