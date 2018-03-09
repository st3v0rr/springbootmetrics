package de.st3v0rr.ms.metrics.transformer;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DropwizardMetricMeasurementTransformerTest {

    private DropwizardMetricMeasurementTransformer transformer;

    @Before
    public void setUp() {
        transformer = new DropwizardMetricMeasurementTransformer();
    }

    @Test
    public void testMeasureNameForCounter() {
        assertEquals("counter", transformer.name("counter.test"));
    }

    @Test
    public void testTagNameForCounter() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("value", "test");
        assertEquals(expectedMap, transformer.tags("counter.test"));
    }

    @Test
    public void testMeasureNameForGauge() {
        assertEquals("gauge", transformer.name("gauge.test"));
    }

    @Test
    public void testTagNameForGauge() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("value", "test");
        assertEquals(expectedMap, transformer.tags("counter.test"));
    }

    @Test
    public void testMeasurenameForClass() {
        assertEquals("de.st3v0rr.ms.metrics.MetricsController", transformer.name("de.st3v0rr.ms.metrics.MetricsController.test"));
    }

    @Test
    public void testTagNameForClass() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("method", "test");
        assertEquals(expectedMap, transformer.tags("de.st3v0rr.ms.metrics.MetricsController.test"));
    }

    @Test
    public void testMeasureNameForAnyOtherValue() {
        assertEquals("de.test.test", transformer.name("de.test.test"));
    }

}
