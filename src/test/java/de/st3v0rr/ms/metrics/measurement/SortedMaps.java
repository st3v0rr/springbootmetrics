package de.st3v0rr.ms.metrics.measurement;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class SortedMaps {
    public static <String, V> SortedMap<String, V> empty() {
        return new TreeMap<>();
    }

    public static <String, V> SortedMap<String, V> singleton(String metricKey, V metricValue) {
        return new TreeMap<>(Collections.singletonMap(metricKey, metricValue));
    }
}
