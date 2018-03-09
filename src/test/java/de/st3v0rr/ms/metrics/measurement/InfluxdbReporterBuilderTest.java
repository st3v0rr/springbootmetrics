package de.st3v0rr.ms.metrics.measurement;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import de.st3v0rr.ms.metrics.transformer.DropwizardMetricMeasurementTransformer;
import de.st3v0rr.ms.metrics.transformer.MetricMeasurementTransformer;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class InfluxdbReporterBuilderTest {

    private final MetricRegistry registry = new MetricRegistry();

    @Test
    public void testBuilderDefault() {
        ScheduledReporter reporter = InfluxdbReporter.forRegistry(registry).build();
        assertNotNull(reporter);
    }

    @Test
    public void testBuilderDefaultResultMetricRegistry() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.registry, registry);
    }

    @Test
    public void testBuilderDefaultResultRateUnit() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.rateUnit, TimeUnit.SECONDS);
    }

    @Test
    public void testBuilderCustomRateUnit() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).convertRatesTo(TimeUnit.MINUTES);
        assertEquals(builder.rateUnit, TimeUnit.MINUTES);
    }

    @Test
    public void testBuilderDefaultResultDurationUnit() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.durationUnit, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testBuilderCustomDurationUnit() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).convertDurationsTo(TimeUnit.SECONDS);
        assertEquals(builder.durationUnit, TimeUnit.SECONDS);
    }

    @Test
    public void testBuilderDefaultResultMetricFilter() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.filter, MetricFilter.ALL);
    }

    @Test
    public void testBuilderCustomMetricFilter() {
        MetricFilter customFilter = new MetricFilter() {
            @Override
            public boolean matches(String s, Metric metric) {
                return false;
            }
        };
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).filter(customFilter);
        assertEquals(builder.filter, customFilter);
    }

    @Test
    public void testBuilderDefaultResultProtocol() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.protocol.getHost(), new HttpInfluxdbProtocol().getHost());
    }

    @Test
    public void testBuilderCustomProtocol() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).protocol(new HttpInfluxdbProtocol("testhost", 80));
        assertEquals(builder.protocol.getHost(), "testhost");
    }

    @Test
    public void testBuilderDefaultResultTransformer() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        Assert.assertEquals(builder.transformer, MetricMeasurementTransformer.DEFAULT);
    }

    @Test
    public void testBuilderCustomTransformer() {
        DropwizardMetricMeasurementTransformer customTransformer = new DropwizardMetricMeasurementTransformer();
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).transformer(customTransformer);
        Assert.assertEquals(builder.transformer, customTransformer);
    }

    @Test
    public void testBuilderDefaultResultTags() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry);
        assertEquals(builder.tags, new HashMap<>());
    }

    @Test
    public void testBuilderCustomTagKey() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).tag("myTagKey", "myTagValue");
        assertTrue(builder.tags.containsKey("myTagKey"));
    }

    @Test
    public void testBuilderCustomTagValue() {
        InfluxdbReporter.Builder builder = InfluxdbReporter.forRegistry(registry).tag("myTagKey", "myTagValue");
        assertTrue(builder.tags.containsValue("myTagValue"));
    }
}
