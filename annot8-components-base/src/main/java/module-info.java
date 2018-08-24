open module io.annot8.components.base {
  requires transitive io.annot8.core;
  requires transitive io.annot8.components.monitor;
  requires slf4j.api;
  requires io.annot8.common.data;
  requires com.google.common;

  exports io.annot8.components.base.components;
  exports io.annot8.components.base.processors;
}
