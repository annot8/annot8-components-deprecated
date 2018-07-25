package io.annot8.components.sources;

import io.annot8.core.settings.Settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class FileSystemSourceSettings implements Settings {

  private Path rootFolder = Paths.get(".");
  private boolean recursive = true;
  private boolean reprocessOnModify = true;
  private Set<Pattern> acceptedFileNamePatterns = new HashSet<>();

  public FileSystemSourceSettings() {
    // Do nothing
  }

  public FileSystemSourceSettings(final Path rootFolder) {
    this.rootFolder = rootFolder;
  }

  public Path getRootFolder() {
    return rootFolder;
  }

  public void setRootFolder(final Path rootFolder) {
    this.rootFolder = rootFolder;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }

  public boolean isReprocessOnModify() {
    return reprocessOnModify;
  }

  public void setReprocessOnModify(boolean reprocessOnModify) {
    this.reprocessOnModify = reprocessOnModify;
  }

  public Set<Pattern> getAcceptedFileNamePatterns() {
    return acceptedFileNamePatterns;
  }

  public void setAcceptedFileNamePatterns(Set<Pattern> acceptedFileNamePatterns) {
    this.acceptedFileNamePatterns = acceptedFileNamePatterns;
  }

  public void addAcceptedFilePattern(Pattern acceptedFilePattern){
    this.acceptedFileNamePatterns.add(acceptedFilePattern);
  }
}
