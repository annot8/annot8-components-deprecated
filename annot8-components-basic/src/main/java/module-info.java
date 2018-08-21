open module io.annot8.components.basic {
  requires transitive io.annot8.core;
  requires io.annot8.common.data;
  requires io.annot8.components.base;
  requires io.annot8.conventions;
  requires com.google.common;

  exports io.annot8.components.processors.annotators;
  exports io.annot8.components.processors.creators;
  exports io.annot8.components.processors.outputs;
  exports io.annot8.components.processors.regex;
}
