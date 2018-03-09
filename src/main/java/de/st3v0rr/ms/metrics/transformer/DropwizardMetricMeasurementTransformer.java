package de.st3v0rr.ms.metrics.transformer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DropwizardMetricMeasurementTransformer implements MetricMeasurementTransformer {
    private final static String SEPARATOR = "\\.";
    private static final String VALUE_TAG_NAME = "value";
    private static final String METHOD_TAG_NAME = "method";
    private static final String COUNTER_METRIC_NAME = "counter";
    private static final String GAUGE_METRIC_NAME = "gauge";

    public DropwizardMetricMeasurementTransformer() {
    }

    @Override
    public Map<String, String> tags(String metricName) {
        Map<String, String> generatedTags = new HashMap<>();
        String[] splitted = metricName.split(SEPARATOR);
        if (isCounterOrGauge(splitted[0])) {
            String[] strings = Arrays.copyOfRange(splitted, 1, splitted.length);
            generatedTags.put(VALUE_TAG_NAME, String.join(".", strings));
        }
        Optional<Class> metricClass = findMetricClass(metricName);
        if (metricClass.isPresent()) {
            String metricClassName = metricClass.get().getSimpleName();
            for (int i = 0; i < splitted.length; i++) {
                if (metricClassName.equals(splitted[i])) {
                    String[] strings = Arrays.copyOfRange(splitted, i + 1, splitted.length);
                    generatedTags.put(METHOD_TAG_NAME, String.join(".", strings));
                    break;
                }
            }
        }
        return generatedTags;
    }

    @Override
    public String name(String metricName) {
        String[] splitted = metricName.split(SEPARATOR);
        if (isCounterOrGauge(splitted[0])) {
            return splitted[0];
        }
        Optional<Class> metricClass = findMetricClass(metricName);
        if (metricClass.isPresent()) {
            return metricClass.get().getName();
        }
        return metricName;
    }

    private boolean isCounterOrGauge(String metricQualifier) {
        return COUNTER_METRIC_NAME.equals(metricQualifier) || GAUGE_METRIC_NAME.equals(metricQualifier);
    }

    private Optional<Class> findMetricClass(String metricName) {
        String[] splittedMeasurementName = metricName.split(SEPARATOR);
        String className = null;
        for (String s : splittedMeasurementName) {
            if (className == null) {
                className = s;
            } else {
                className += "." + s;
            }
            try {
                Class<?> metricClass = Class.forName(className);
                return Optional.of(metricClass);
            } catch (ClassNotFoundException e) {
                //NOP
            }
        }
        return Optional.empty();
    }
}
