package io.annot8.components.mongo.data;

import java.util.Collection;
import java.util.Map;

public class ContentDto {

  private String id;
  private String name;
  private Object data;
  private Map<String, Object> properties;
  private Collection<AnnotationDto> annotations;

  public ContentDto(String name, Object data,
      Map<String, Object> properties, Collection<AnnotationDto> annotations){
    this.id = id;
    this.name = name;
    this.data = data;
    this.properties = properties;
    this.annotations = annotations;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public Collection<AnnotationDto> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(
      Collection<AnnotationDto> annotations) {
    this.annotations = annotations;
  }
}
