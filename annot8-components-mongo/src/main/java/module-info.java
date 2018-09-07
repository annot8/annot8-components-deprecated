module io.annot8.components.mongo{
  requires io.annot8.common.data;
  requires io.annot8.components.base;
  requires io.annot8.conventions;
  
  requires mongo.java.driver;
  
  exports io.annot8.components.mongo;
  exports io.annot8.components.mongo.data;
  exports io.annot8.components.mongo.processors;
  exports io.annot8.components.mongo.resources;
  exports io.annot8.components.mongo.sources;
}