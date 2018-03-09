package de.st3v0rr.ms.metrics.measurement;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import de.st3v0rr.ms.metrics.transformer.MetricMeasurementTransformer;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MeasurementReporter extends ScheduledReporter {
    private final Sender sender;
    private final Clock clock;
    private final Map<String, String> baseTags;
    private final MetricMeasurementTransformer transformer;

    public MeasurementReporter(Sender sender, MetricRegistry registry, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, Clock clock, Map<String, String> baseTags, MetricMeasurementTransformer transformer) {
        super(registry, "measurement-reporter", filter, rateUnit, durationUnit);
        this.baseTags = baseTags;
        this.sender = sender;
        this.clock = clock;
        this.transformer = transformer;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges
            , SortedMap<String, Counter> counters
            , SortedMap<String, Histogram> histograms
            , SortedMap<String, Meter> meters
            , SortedMap<String, Timer> timers) {

        final long timestamp = clock.getTime();
        List<Point> points = new ArrayList<>();

        for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
            points.add(fromGauge(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Map.Entry<String, Counter> entry : counters.entrySet()) {
            points.add(fromCounter(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
            points.add(fromHistogram(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Map.Entry<String, Meter> entry : meters.entrySet()) {
            points.add(fromMeter(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Map.Entry<String, Timer> entry : timers.entrySet()) {
            points.add(fromTimer(entry.getKey(), entry.getValue(), timestamp));
        }

        sender.send(points);
    }

    private Point fromTimer(String metricName, Timer t, long timestamp) {
        Snapshot snapshot = t.getSnapshot();

        Map<String, String> tags = new HashMap<>(baseTags);
        tags.putAll(transformer.tags(metricName));

        return Point.measurement(transformer.name(metricName))
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(tags)
                .addField("count", snapshot.size())
                .addField("min", convertDuration(snapshot.getMin()))
                .addField("max", convertDuration(snapshot.getMax()))
                .addField("mean", convertDuration(snapshot.getMean()))
                .addField("std-dev", convertDuration(snapshot.getStdDev()))
                .addField("50-percentile", convertDuration(snapshot.getMedian()))
                .addField("75-percentile", convertDuration(snapshot.get75thPercentile()))
                .addField("95-percentile", convertDuration(snapshot.get95thPercentile()))
                .addField("99-percentile", convertDuration(snapshot.get99thPercentile()))
                .addField("999-percentile", convertDuration(snapshot.get999thPercentile()))
                .addField("one-minute", convertRate(t.getOneMinuteRate()))
                .addField("five-minute", convertRate(t.getFiveMinuteRate()))
                .addField("fifteen-minute", convertRate(t.getFifteenMinuteRate()))
                .addField("mean-minute", convertRate(t.getMeanRate()))
                .addField("run-count", t.getCount())
                .build();
    }

    private Point fromMeter(String metricName, Meter mt, long timestamp) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.putAll(transformer.tags(metricName));

        return Point.measurement(transformer.name(metricName))
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(tags)
                .addField("count", mt.getCount())
                .addField("one-minute", convertRate(mt.getOneMinuteRate()))
                .addField("five-minute", convertRate(mt.getFiveMinuteRate()))
                .addField("fifteen-minute", convertRate(mt.getFifteenMinuteRate()))
                .addField("mean-minute", convertRate(mt.getMeanRate()))
                .build();
    }

    private Point fromHistogram(String metricName, Histogram h, long timestamp) {
        Snapshot snapshot = h.getSnapshot();

        Map<String, String> tags = new HashMap<>(baseTags);
        tags.putAll(transformer.tags(metricName));

        return Point.measurement(transformer.name(metricName))
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(tags)
                .addField("count", snapshot.size())
                .addField("min", snapshot.getMin())
                .addField("max", snapshot.getMax())
                .addField("mean", snapshot.getMean())
                .addField("std-dev", snapshot.getStdDev())
                .addField("50-percentile", snapshot.getMedian())
                .addField("75-percentile", snapshot.get75thPercentile())
                .addField("95-percentile", snapshot.get95thPercentile())
                .addField("99-percentile", snapshot.get99thPercentile())
                .addField("999-percentile", snapshot.get999thPercentile())
                .addField("run-count", h.getCount())
                .build();
    }

    private Point fromCounter(String metricName, Counter c, long timestamp) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.putAll(transformer.tags(metricName));

        return Point.measurement(transformer.name(metricName))
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(tags)
                .addField("count", c.getCount())
                .build();
    }

    private Point fromGauge(String metricName, Gauge g, long timestamp) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.putAll(transformer.tags(metricName));

        Point.Builder pointBuilder = Point.measurement(transformer.name(metricName))
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(tags);

        Object o = g.getValue();
        if (o == null) {
            // skip null values
            return null;
        }
        if (o instanceof Long || o instanceof Integer) {
            long value = ((Number) o).longValue();
            pointBuilder.addField("value", value);
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d.isInfinite() || d.isNaN()) {
                // skip Infinite & NaN
                return null;
            }
            pointBuilder.addField("value", d.doubleValue());
        } else if (o instanceof Float) {
            Float f = (Float) o;
            if (f.isInfinite() || f.isNaN()) {
                // skip Infinite & NaN
                return null;
            }
            pointBuilder.addField("value", f.floatValue());
        } else {
            String value = "" + o;
            pointBuilder.addField("value", value);
        }

        return pointBuilder.build();
    }
}
