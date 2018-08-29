package io.annot8.components.monitor.resources;

import io.annot8.components.monitor.resources.metering.Metrics;
import io.annot8.core.components.Annot8Component;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MeteringTest {

    @Test
    public void testMetering(){
        Metrics metrics = Metering.useGlobalRegistry().getMetrics(TestProcessor.class);
        assertNotNull(metrics);
    }

    @Test
    public void testUseMeterRegistry(){
        MeterRegistry registry = Mockito.mock(MeterRegistry.class);
        Metrics metrics = Metering.useMeterRegistry(registry).getMetrics(TestProcessor.class);
        assertNotNull(metrics);
    }

    @Test
    public void testNotAvailable(){
        Metrics metrics = Metering.notAvailable().getMetrics(TestProcessor.class);
        assertNotNull(metrics);
    }

    private abstract class TestProcessor implements Annot8Component{}

}