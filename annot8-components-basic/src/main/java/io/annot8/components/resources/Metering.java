package io.annot8.components.resources;

import java.util.Collection;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import io.annot8.components.base.components.AbstractComponent;
import io.annot8.components.resources.metering.Metrics;
import io.annot8.components.resources.metering.NamedMetrics;
import io.annot8.core.components.Resource;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.lang.Nullable;

public final class Metering implements Resource {

	private final MeterRegistry meterRegistry;

	protected Metering(boolean useGlobalMetrics, MeterRegistry meterRegistry) {
		// Ensure that we have at least something to create a logger with

		if (useGlobalMetrics) {
			this.meterRegistry = io.micrometer.core.instrument.Metrics.globalRegistry;
		} else if (meterRegistry != null) {
			this.meterRegistry = meterRegistry;
		} else {
			this.meterRegistry = new SimpleMeterRegistry();
		}
	}
	
	public Metrics getMetrics(Class<? extends AbstractComponent> clazz) {
		return new NamedMetrics(meterRegistry, clazz);
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


}
