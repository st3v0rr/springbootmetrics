//	metrics-influxdb
//
//	Written in 2014 by David Bernard <dbernard@novaquark.com>
//
//	[other author/contributor lines as appropriate]
//
//	To the extent possible under law, the author(s) have dedicated all copyright and
//	related and neighboring rights to this software to the public domain worldwide.
//	This software is distributed without any warranty.
//
//	You should have received a copy of the CC0 Public Domain Dedication along with
//	this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
package de.st3v0rr.ms.metrics.measurement;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import de.st3v0rr.ms.metrics.transformer.MetricMeasurementTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class InfluxdbReporter {

    private InfluxdbReporter() {
    }

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public static class Builder {

        final MetricRegistry registry;
        private final Clock clock;
        TimeUnit rateUnit;
        TimeUnit durationUnit;
        MetricFilter filter;
        HttpInfluxdbProtocol protocol;
        final Map<String, String> tags;
        MetricMeasurementTransformer transformer = MetricMeasurementTransformer.DEFAULT;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.protocol = new HttpInfluxdbProtocol();
            this.tags = new HashMap<>();
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder protocol(HttpInfluxdbProtocol protocol) {
            Objects.requireNonNull(protocol, "InfluxdbProtocol cannot be null");
            this.protocol = protocol;
            return this;
        }

        public Builder transformer(MetricMeasurementTransformer transformer) {
            Objects.requireNonNull(transformer, "MetricMeasurementTransformer cannot be null");
            this.transformer = transformer;
            return this;
        }

        public Builder tag(String tagKey, String tagValue) {
            Objects.requireNonNull(tagKey, "tagKey cannot be null");
            Objects.requireNonNull(tagValue, "tagValue cannot be null");
            tags.put(tagKey, tagValue);
            return this;
        }

        public ScheduledReporter build() {
            Sender s = new SenderImpl(protocol);
            return new MeasurementReporter(s, registry, filter, rateUnit, durationUnit, clock, tags, transformer);
        }
    }
}
