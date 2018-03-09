# Beispielapplikation für Metriken

## Verwendung
### Installation Infrastruktur
[InfluxDB](https://docs.influxdata.com/influxdb)
[Grafana](http://docs.grafana.org/guides/getting_started/)

### Starten
Application starten mit dev Profil
--spring.profiles.active=dev

``` java
@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## Experimentieren
Die Anwendung besitzt 3 REST Endpunkte

<b>http://localhost:8080</b> 

<b>/testservice/http_ok200</b> 
<b>/testservice/http_error404</b> 
<b>/testservice/http_error500</b> 

Diese können über Postman ohne weitere Parameter aufgerufen werden und leifern die passenden Responses.

Zusätzlich gibt es einen 4. REST Endpunkt

<b>http://localhost:8080/management/metrics</b>

Hier sind die Metriken aller Messpunkte einsehbar. Der Zugang ist konfiguriert in den application-dev.properties. 

```properties
management.context-path=/management
MANAGEMENT_USER_NAME=admin
MANAGEMENT_USER_PASSWORD=secret
```

## Wichtige Code-Snippets
### Metriken erfassen
de.st3v0rr.ms.metrics.Application
```java
@EnableMetrics //Metriken aktivieren
@SpringBootApplication
public class Application {
    //... 
}
```

de.st3v0rr.ms.metrics.MetricsController
```java
@Timed //Messung für diesen Service aktivieren
@GetMapping(path = "/http_ok")
public ResponseEntity<String> getHttpOk() {
    //...
}
```

### Metriken exportieren mittels Adapter
Die in der Anwendung enthaltenen Adapter exportieren in einem definierten Zeitintervall die erfassten Metriken.

Aktiviert und gesteuert werden die Einstellungen über die application-dev.properties.

Im folgenden Beispiel wird ein ConsoleReporter aktiviert, der alle erfassten Metriken im 10 Sekunden Takt in die Console schreibt.

```properties
REPORTER_CONSOLE_ENABLED=true
REPORTER_TIMEUNIT=SECONDS
REPORTER_TIMEPERIOD=10
```

de.st3v0rr.ms.metrics.adapter.ConsoleMetricsConfigurerAdapter
```java
@Configuration
@ConditionalOnProperty(name = "REPORTER_CONSOLE_ENABLED", havingValue = "true")
public class ConsoleMetricsConfigurerAdapter extends MetricsConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleMetricsConfigurerAdapter.class);
    private final String reporterTimeUnit;
    private final String reporterTimeperiod;

    @Autowired
    public ConsoleMetricsConfigurerAdapter(@Value("#{environment.REPORTER_TIMEUNIT}") final String reporterTimeUnit,
                                         @Value("#{environment.REPORTER_TIMEPERIOD}") final String reporterTimeperiod) {
        super();
        this.reporterTimeUnit = reporterTimeUnit;
        this.reporterTimeperiod = reporterTimeperiod;
    }

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        LOG.info("registering Console Reporter");
        LOG.info("reporter interval " + reporterTimeperiod + " " + reporterTimeUnit);
        registerReporter(ConsoleReporter
                .forRegistry(metricRegistry)
                .build())
                .start(Long.valueOf(reporterTimeperiod), TimeUnit.valueOf(reporterTimeUnit));
    }

}

```

### Metriken exportieren in InfluxDB
Beim senden der Daten an eine InfluxDB ist darauf zu achten, dass wenige Measurements und viele Tags verwendet werden.

Dies sorgt für einen Ressourcenschonenden Umgang und hohe Performance.

de.st3v0rr.ms.metrics.transformer.DropwizardMetricMeasurementTransformer

Hier werden die Metriken auf die Measurements "gauge", "counter", und die Serviceklassen heruntergebrochen und alle weiteren Kriterien als Tags gekennzeichnet.

## Konfiguration
In der application-dev.properties sind weitere wichtige Parameter.
Informationen hierzu kann man den 3rd Party Libs Dokumentationen entnehmen.
```properties
# MetricsSecurityConfig */
management.security.enabled=true
management.security.roles=BASIC_AUTH_AUTHENTICATED
management.context-path=/management
 
MANAGEMENT_USER_NAME=admin
MANAGEMENT_USER_PASSWORD=secret
 
# MetricsAdapterConfiguration für Console, Slf4J */
REPORTER_CONSOLE_ENABLED=true
REPORTER_SLF4J_ENABLED=true
REPORTER_INFLUXDB_ENABLED=true
REPORTER_INFLUXDB_REPORTERENVIRONMENT=testenv
REPORTER_INFLUXDB_REPORTERHOST=localhost
REPORTER_INFLUXDB_REPORTERPORT=8086
REPORTER_INFLUXDB_REPORTERUSER=username
REPORTER_INFLUXDB_REPORTERPASSWORD=password
REPORTER_INFLUXDB_REPORTERDATABASE=databasename
REPORTER_TIMEUNIT=SECONDS
REPORTER_TIMEPERIOD=10
 
# Spring Boot Actuator Konfiguration */
endpoints.enabled=false
endpoints.metrics.enabled=true
```

## Testing
Metriken können auf Integrationstestebene getestet werden.

Beispiel für einen Integrationstest
```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationMetricsEndpointTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "secret";
    private static final String METRICS_ENDPOINT = "/metrics";

    @LocalServerPort
    int port;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test
    public void testMetricsEndpoint_ContainsMethodHttpOk() {
        ResponseEntity<String> healthEntity = getStringResponseEntity();
        Assert.assertTrue(healthEntity.getBody().contains("getHttpOk"));
    }
    @Test
    public void testMetricsEndpoint_ContainsMethodHttpError404() {
        ResponseEntity<String> healthEntity = getStringResponseEntity();
        Assert.assertTrue(healthEntity.getBody().contains("getHttpError404"));
    }
    @Test
    public void testMetricsEndpoint_ContainsMethodHttpError500() {
        ResponseEntity<String> healthEntity = getStringResponseEntity();
        Assert.assertTrue(healthEntity.getBody().contains("getHttpError500"));
    }

    private ResponseEntity<String> getStringResponseEntity() {
        return testRestTemplate.withBasicAuth(USERNAME, PASSWORD).getForEntity(buildManagementUrl() + METRICS_ENDPOINT, String.class);
    }

    private String buildManagementUrl() {
        return "http://localhost:" + port + "/management";
    }

}
```

## Dependencies
3rd Party Libs für weitere Informationen:

[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready)
[Spring Boot Security](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-security) 
[Dropwizard Metrics](http://metrics.dropwizard.io)
[Spring-Metrics](http://metrics.ryantenney.com/)
