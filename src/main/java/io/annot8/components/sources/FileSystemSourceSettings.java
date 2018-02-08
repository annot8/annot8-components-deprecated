package io.annot8.components.sources;

import io.annot8.core.settings.Settings;

public class FileSystemSourceSettings implements Settings {

  private String rootFolder = ".";

  public FileSystemSourceSettings() {
    // Do nothing
  }

  public FileSystemSourceSettings(final String rootFolder) {
    this.rootFolder = rootFolder;
  }

  public String getRootFolder() {
    return rootFolder;
  }

  public void setRootFolder(final String rootFolder) {
    this.rootFolder = rootFolder;
  }

}
