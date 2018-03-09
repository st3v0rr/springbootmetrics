package de.st3v0rr.ms.metrics.adapter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "REPORTER_SLF4J_ENABLED", havingValue = "true")
public class Slf4jMetricsConfigurerAdapter extends MetricsConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jMetricsConfigurerAdapter.class);
    private final String reporterTimeUnit;
    private final String reporterTimeperiod;

    @Autowired
    public Slf4jMetricsConfigurerAdapter(@Value("#{environment.REPORTER_TIMEUNIT}") final String reporterTimeUnit,
                                         @Value("#{environment.REPORTER_TIMEPERIOD}") final String reporterTimeperiod) {
        super();
        this.reporterTimeUnit = reporterTimeUnit;
        this.reporterTimeperiod = reporterTimeperiod;
    }

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        LOG.info("registering Slf4J Reporter");
        LOG.info("reporter interval " + reporterTimeperiod + " " + reporterTimeUnit);
        registerReporter(Slf4jReporter
                .forRegistry(metricRegistry)
                .build())
                .start(Long.valueOf(reporterTimeperiod), TimeUnit.valueOf(reporterTimeUnit));
    }

}
