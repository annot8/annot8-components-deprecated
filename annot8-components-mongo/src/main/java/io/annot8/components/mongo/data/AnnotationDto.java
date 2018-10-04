/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.mongo.data;

import java.util.Map;

import io.annot8.core.bounds.Bounds;

public class AnnotationDto {

  private String id;
  private String type;
  private Map<String, Object> properties;
  private Bounds bounds;
  private Object data;
  private String contentId;

  public AnnotationDto(
      String id,
      String type,
      Bounds bounds,
      Object data,
      Map<String, Object> properties,
      String contentId) {
    this.id = id;
    this.type = type;
    this.properties = properties;
    this.bounds = bounds;
    this.data = data;
    this.contentId = contentId;
  }

  public String getId() {
    return id;
  }

  public Bounds getBounds() {
    return bounds;
  }

  public Object getData() {
    return data;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public String getType() {
    return type;
  }

  public String getContentId() {
    return contentId;
  }
}
