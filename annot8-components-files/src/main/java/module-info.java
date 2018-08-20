module io.annot8.components.files {
  requires transitive io.annot8.core;
  requires io.annot8.common.data;
  requires io.annot8.components.base;
  requires com.google.common;

  exports io.annot8.components.files.sources;
  exports io.annot8.components.files.processors;
}