open module io.annot8.components.quantities {
  requires transitive io.annot8.core;
  requires io.annot8.common.data;
  requires io.annot8.conventions;

  requires transitive io.annot8.components.base;

  requires slf4j.api;
  
  exports io.annot8.components.quantities.processors;
}
