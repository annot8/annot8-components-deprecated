/**
 * This module contains common functionality for the Annot8 framework, including helper and utility
 * functions, abstract classes, factory objects, and common implementations of some components
 * (notably Bounds).
 *
 * It does not contain default implementations of the majority of components, which are held in the
 * default-impl module.
 *
 * The abstract classes in this module are there to provide correct implementations of functions
 * such as equals, hashCode and toString. They do not provide any logic beyond this, and should
 * generally be used by any implementations of the interfaces they are abstracting.
 */
open module io.annot8.components.base {
  requires transitive io.annot8.core;
  requires io.annot8.components.resources.monitor;
  requires micrometer.core;
  requires slf4j.api;
  requires io.annot8.common.data;

  exports io.annot8.components.base.components;
  exports io.annot8.components.base.processors;

}
