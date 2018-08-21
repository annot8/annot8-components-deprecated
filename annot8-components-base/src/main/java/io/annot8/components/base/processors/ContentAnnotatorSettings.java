package io.annot8.components.base.processors;

import io.annot8.core.settings.Settings;
import java.util.Set;

public class ContentAnnotatorSettings implements Settings {

  // List of view name to consider
  // null/empty implies all
  private Set<String> content;

  public ContentAnnotatorSettings(Set<String> content) {
    this.content = content;
  }

  public Set<String> getContent() {
    return content;
  }

  public void setContent(Set<String> content) {
    this.content = content;
  }
}
