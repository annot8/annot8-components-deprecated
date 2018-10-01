package io.annot8.components.mongo.sinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import io.annot8.common.serialisation.jackson.Annot8ObjectMapperFactory;
import io.annot8.components.mongo.AbstractMongoComponent;
import io.annot8.components.mongo.data.AnnotationDto;
import io.annot8.components.mongo.data.ContentDto;
import io.annot8.components.mongo.data.ItemDto;
import io.annot8.components.mongo.resources.Mongo;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.capabilities.UsesResource;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.Document;

@UsesResource(Mongo.class)
public class MongoSink extends AbstractMongoComponent implements Processor {

  private MongoCollection<Document> collection;
  private ObjectMapper mapper;

  @Override
  protected void configure(Context context, MongoConnection connection) {
    collection = connection.getCollection();
    mapper = new ObjectMapper();
    Annot8ObjectMapperFactory factory = new Annot8ObjectMapperFactory();
    factory.scan();
    factory.configure(mapper);
  }

  @Override
  public ProcessorResponse process(Item item) {
    String parentId = null;
    if(item.getParent().isPresent()){
      parentId = item.getParent().get();
    }

    ItemDto itemDto = new ItemDto(item.getId(), parentId,
        item.getProperties().getAll(), getContents(item));

    String json = null;
    try {
      json = mapper.writeValueAsString(itemDto);
    } catch (JsonProcessingException e) {
      log().error("Failed to serialize item", e);
      return ProcessorResponse.itemError();
    }
    Document document = Document.parse(json);

    collection.insertOne(document);

    return ProcessorResponse.ok();
  }

  private Collection<ContentDto> getContents(Item item){
    return item.getContents()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  private Collection<AnnotationDto> getAnnotations(Content content){
    return content.getAnnotations().getAll()
        .map((a) -> toDto(a, content))
        .collect(Collectors.toList());
  }

  private ContentDto toDto(Content content){
    return new ContentDto(content.getName(), content.getData(),
        content.getProperties().getAll(), getAnnotations(content));
  }

  private AnnotationDto toDto(Annotation annotation, Content content){
    Object data = null;
    Optional<Object> optional = annotation.getBounds().getData(content);
    if(optional.isPresent()){
      data = optional.get();
    }
    return new AnnotationDto(annotation.getId(),annotation.getType(),
        annotation.getBounds(), data, annotation.getProperties().getAll());
  }

}
