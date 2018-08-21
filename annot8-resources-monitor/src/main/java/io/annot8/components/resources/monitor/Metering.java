package io.annot8.components.resources.monitor;

import io.annot8.components.resources.monitor.metering.Metrics;
import io.annot8.components.resources.monitor.metering.NamedMetrics;
import io.annot8.core.components.Annot8Component;
import io.annot8.core.components.Resource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Objects;

public final class Metering implements Resource {

  private final MeterRegistry meterRegistry;

  protected Metering(boolean useGlobalMetrics, MeterRegistry meterRegistry) {
    // Ensure that we have at least something to create a logger with

    if (useGlobalMetrics) {
      this.meterRegistry = io.micrometer.core.instrument.Metrics.globalRegistry;
    } else
      this.meterRegistry = Objects.requireNonNullElseGet(meterRegistry, SimpleMeterRegistry::new);
  }

  public static Metering useGlobalRegistry() {
    return new Metering(true, null);
  }

  public static Metering useMeterRegistry(MeterRegistry meterRegistry) {
    return new Metering(false, meterRegistry);
  }

  public static Metering notAvailable() {
    return new Metering(false, null);
  }

  public Metrics getMetrics(Class<? extends Annot8Component> clazz) {
    return new NamedMetrics(meterRegistry, clazz);
  }


}
