package io.annot8.components.mongo.data;

import io.annot8.core.bounds.Bounds;
import java.util.Map;

public class AnnotationDto {

  private String id;
  private String type;
  private Map<String, Object>  properties;
  private Bounds bounds;
  private Object data;

  public AnnotationDto(String id, String type, Bounds bounds, Object data, Map<String, Object> properties){
    this.id = id;
    this.type = type;
    this.properties = properties;
    this.bounds = bounds;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
