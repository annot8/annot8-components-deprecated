open module io.annot8.components {
  requires transitive io.annot8.defaultimpl;
  requires io.annot8.common.components;
  requires io.annot8.conventions;
  requires com.google.common;

  exports io.annot8.components.processors.annotators;
  exports io.annot8.components.processors.creators;
  exports io.annot8.components.processors.file;
  exports io.annot8.components.processors.outputs;
  exports io.annot8.components.processors.regex;
  exports io.annot8.components.sources;
  
  requires slf4j.api;
  requires micrometer.core;
}
