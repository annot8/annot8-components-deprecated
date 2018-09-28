module io.annot8.components.tika {
  requires transitive io.annot8.core;
  requires transitive io.annot8.common.data;
  requires transitive io.annot8.components.base;

  requires slf4j.api;

  requires org.apache.tika.core;
  requires org.apache.tika.parsers;
  requires java.xml;
}