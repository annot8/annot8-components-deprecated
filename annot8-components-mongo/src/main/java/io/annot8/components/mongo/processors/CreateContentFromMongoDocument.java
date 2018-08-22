package io.annot8.components.mongo.processors;

import io.annot8.common.data.content.Text;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.components.mongo.data.MongoDocument;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import org.bson.Document;


public class CreateContentFromMongoDocument extends AbstractComponent implements Processor {

  @Override
  public ProcessorResponse process(Item item) {
    item.getContents(MongoDocument.class).forEach(d -> {
      Document doc = d.getData();
      for(String key : doc.keySet()){
        Object o = doc.get(key);
        if(o instanceof String){
          try {
            item.create(Text.class)
                .withName(key)
                .withData(o.toString())
            .save();
          } catch (Annot8Exception e) {
            log().warn("Couldn't create content for field {}", key, e);
          }
        }

        //TODO: Handle other types - e.g. nested objects, numbers, booleans, etc.
      }
    });

    return ProcessorResponse.ok();
  }
}
