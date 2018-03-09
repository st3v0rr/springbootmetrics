package de.st3v0rr.ms.metrics.adapter;

import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import de.st3v0rr.ms.metrics.measurement.InfluxdbReporter;
import de.st3v0rr.ms.metrics.measurement.HttpInfluxdbProtocol;
import de.st3v0rr.ms.metrics.transformer.DropwizardMetricMeasurementTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "REPORTER_INFLUXDB_ENABLED", havingValue = "true")
public class InfluxDbMetricsConfigurerAdapter extends MetricsConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDbMetricsConfigurerAdapter.class);
    private final String reporterEnvironment;
    private final String reporterHost;
    private final String reporterPort;
    private final String reporterUser;
    private final String reporterPassword;
    private final String reporterDatabase;
    private final String reporterTimeUnit;
    private final String reporterTimeperiod;

    @Autowired
    public InfluxDbMetricsConfigurerAdapter(@Value("#{environment.REPORTER_INFLUXDB_REPORTERENVIRONMENT}") final String reporterEnvironment,
                                            @Value("#{environment.REPORTER_INFLUXDB_REPORTERHOST}") final String reporterHost,
                                            @Value("#{environment.REPORTER_INFLUXDB_REPORTERPORT}") final String reporterPort,
                                            @Value("#{environment.REPORTER_INFLUXDB_REPORTERUSER}") final String reporterUser,
                                            @Value("#{environment.REPORTER_INFLUXDB_REPORTERPASSWORD}") final String reporterPassword,
                                            @Value("#{environment.REPORTER_INFLUXDB_REPORTERDATABASE}") final String reporterDatabase,
                                            @Value("#{environment.REPORTER_TIMEUNIT}") final String reporterTimeUnit,
                                            @Value("#{environment.REPORTER_TIMEPERIOD}") final String reporterTimeperiod) {
        super();
        this.reporterEnvironment = reporterEnvironment;
        this.reporterHost = reporterHost;
        this.reporterPort = reporterPort;
        this.reporterUser = reporterUser;
        this.reporterPassword = reporterPassword;
        this.reporterDatabase = reporterDatabase;
        this.reporterTimeUnit = reporterTimeUnit;
        this.reporterTimeperiod = reporterTimeperiod;
    }

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        LOG.info("registering InfluxDB Reporter for influxdb http://" + reporterHost + ":" + reporterPort + " on database " + reporterDatabase + " with user " + reporterUser + " and password " + getPseudoPassword());
        LOG.info("reporter interval " + reporterTimeperiod + " " + reporterTimeUnit);
        registerReporter(InfluxdbReporter
                .forRegistry(metricRegistry)
                .protocol(new HttpInfluxdbProtocol(reporterHost, Integer.valueOf(reporterPort), reporterUser, reporterPassword, reporterDatabase))
                .transformer(new DropwizardMetricMeasurementTransformer())
                .tag("environment", reporterEnvironment)
                .build())
                .start(Long.valueOf(reporterTimeperiod), TimeUnit.valueOf(reporterTimeUnit));
    }

    private String getPseudoPassword() {
        return reporterPassword != null ? "***" : "null";
    }

}
