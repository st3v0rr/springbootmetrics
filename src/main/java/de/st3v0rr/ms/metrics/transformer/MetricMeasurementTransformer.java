package de.st3v0rr.ms.metrics.transformer;

import java.util.Collections;
import java.util.Map;

public interface MetricMeasurementTransformer {
    Map<String, String> tags(String metricName);

    String name(String metricName);

    MetricMeasurementTransformer DEFAULT = new MetricMeasurementTransformer() {
        @Override
        public Map<String, String> tags(String metricName) {
            return Collections.emptyMap();
        }

        @Override
        public String name(String metricName) {
            return metricName;
        }
    };
}
