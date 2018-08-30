module io.annot8.components.tika {
  requires transitive io.annot8.core;
  requires transitive io.annot8.common.data;
  requires transitive io.annot8.components.base;

  requires slf4j.api;

  requires transitive tika.core;
  requires transitive tika.parsers;
}