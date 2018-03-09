package de.st3v0rr.ms.metrics.measurement;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import de.st3v0rr.ms.metrics.transformer.MetricMeasurementTransformer;
import org.influxdb.dto.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MeasurementReporterTest {
    private SenderTestImpl sender;
    private MetricRegistry registry;
    private MeasurementReporter reporter;

    class SenderTestImpl implements Sender {

        private final List<Point> points;

        SenderTestImpl() {
            points = new ArrayList<>();
        }

        @Override
        public void send(List<Point> metricPoints) {
            points.addAll(metricPoints);
        }

        List<Point> getPoints() {
            return points;
        }
    }

    @Before
    public void setUp() {
        sender = new SenderTestImpl();
        registry = new MetricRegistry();
        reporter = new MeasurementReporter(sender, registry, null, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, Clock.defaultClock(), Collections.emptyMap(), MetricMeasurementTransformer.DEFAULT);
    }

    @Test
    public void testBaseTags() {
        String serverKey = "tagkey";
        String serverName = "tagvalue";
        Map<String, String> baseTags = new HashMap<>();
        baseTags.put(serverKey, serverName);
        reporter = new MeasurementReporter(sender, registry, null, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, Clock.defaultClock(), baseTags, MetricMeasurementTransformer.DEFAULT);
        reporter.report(SortedMaps.empty(), SortedMaps.singleton("c", new Counter()), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("tagkey=tagvalue"));
    }

    @Test
    public void testEmptyPoints() {
        assertEquals(0, sender.getPoints().size());
    }

    @Test
    public void testCounterPointSize() {
        String counterName = "c";
        Counter c = registry.counter(counterName);
        c.inc();
        reporter.report(SortedMaps.empty(), SortedMaps.singleton(counterName, c), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty());
        assertEquals(1, sender.getPoints().size());
    }

    @Test
    public void testCounterPointName() {
        String counterName = "c";
        Counter c = registry.counter(counterName);
        c.inc();
        reporter.report(SortedMaps.empty(), SortedMaps.singleton(counterName, c), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("c count=1i"));
    }

    @Test
    public void testGaugePointSize() {
        String gaugeName = "g";
        Gauge<Integer> g = () -> 0;
        reporter.report(SortedMaps.singleton(gaugeName, g), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty());
        assertEquals(1, sender.getPoints().size());
    }

    @Test
    public void testGaugePointName() {
        String gaugeName = "g";
        Gauge<Integer> g = () -> 0;
        reporter.report(SortedMaps.singleton(gaugeName, g), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("g value=0i"));
    }

    private Meter getMeterMock(String meterName) {
        Meter meter = registry.meter(meterName);
        meter.mark();
        return meter;
    }

    @Test
    public void testMeterPointSize() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertEquals(1, sender.getPoints().size());
    }

    @Test
    public void testMeterPointName() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().startsWith("m"));
    }

    @Test
    public void testMeterPointContainsCount() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("count=1i"));
    }

    @Test
    public void testMeterPointContainsOneMinuteRate() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("one-minute="));
    }

    @Test
    public void testMeterPointContainsFiveMinuteRate() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("five-minute="));
    }

    @Test
    public void testMeterPointContainsFifteenMinuteRate() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("fifteen-minute="));
    }

    @Test
    public void testMeterPointContainsMeanMinuteRate() {
        String meterName = "m";
        Meter meter = getMeterMock(meterName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(meterName, meter), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("mean-minute="));
    }

    private Histogram getHistogramMock(String histogramName) {
        Histogram histogram = registry.histogram(histogramName);
        histogram.update(0);
        return histogram;
    }

    @Test
    public void testHistogramPointSize() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertEquals(1, sender.getPoints().size());
    }

    @Test
    public void testHistogramPointName() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().startsWith("h"));
    }

    @Test
    public void testHistogramPointContainsCount() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("count=1i"));
    }

    @Test
    public void testHistogramPointContainsMin() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("min="));
    }

    @Test
    public void testHistogramPointContainsMax() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("max="));
    }

    @Test
    public void testHistogramPointContainsMean() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("mean="));
    }

    @Test
    public void testHistogramPointContainsStdDev() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("std-dev="));
    }

    @Test
    public void testHistogramPointContains50Percentile() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("50-percentile="));
    }

    @Test
    public void testHistogramPointContains75Percentile() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("75-percentile="));
    }

    @Test
    public void testHistogramPointContains95Percentile() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("95-percentile="));
    }

    @Test
    public void testHistogramPointContains99Percentile() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("99-percentile="));
    }

    @Test
    public void testHistogramPointContains999Percentile() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("999-percentile="));
    }

    @Test
    public void testHistogramPointContainsRunCount() {
        String histogramName = "h";
        Histogram histogram = getHistogramMock(histogramName);
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(histogramName, histogram), SortedMaps.empty(), SortedMaps.empty());
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("run-count="));
    }

    @Test
    public void testTimerPointSize() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertEquals(1, sender.getPoints().size());
    }

    @Test
    public void testTimerPointName() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().startsWith("t"));
    }

    @Test
    public void testTimerPointContainsCount() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("count=1i"));
    }

    @Test
    public void testTimerPointContainsOneMinute() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("one-minute="));
    }

    @Test
    public void testTimerPointContainsFiveMinute() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("five-minute="));
    }

    @Test
    public void testTimerPointContainsFifteenMinute() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("fifteen-minute="));
    }

    @Test
    public void testTimerPointContainsMeanMinute() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("mean-minute="));
    }

    @Test
    public void testTimerPointContainsMin() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("min="));
    }

    @Test
    public void testTimerPointContainsMax() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("max="));
    }

    @Test
    public void testTimerPointContainsMean() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("mean="));
    }

    @Test
    public void testTimerPointContainsStdDev() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("std-dev="));
    }

    @Test
    public void testTimerPointContains50Percentile() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("50-percentile="));
    }

    @Test
    public void testTimerPointContains75Percentile() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("75-percentile="));
    }

    @Test
    public void testTimerPointContains95Percentile() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("95-percentile="));
    }

    @Test
    public void testTimerPointContains99Percentile() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("99-percentile="));
    }

    @Test
    public void testTimerPointContains999Percentile() {
        String timerName = "t";
        Timer meter = registry.timer(timerName);
        Timer.Context ctx = meter.time();
        ctx.stop();
        reporter.report(SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.empty(), SortedMaps.singleton(timerName, meter));
        assertTrue(sender.getPoints().get(0).lineProtocol().contains("999-percentile="));
    }

}
