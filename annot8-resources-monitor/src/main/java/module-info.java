module io.annot8.components.resources.monitor {
  requires transitive io.annot8.core;
  requires slf4j.api;
  requires micrometer.core;

  exports io.annot8.components.resources.monitor;
  exports io.annot8.components.resources.monitor.metering;

}