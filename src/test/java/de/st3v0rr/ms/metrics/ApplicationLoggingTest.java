package de.st3v0rr.ms.metrics;

import org.junit.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ApplicationLoggingTest {

    @LocalServerPort
    int port;

    @ClassRule
    public static final OutputCapture capture = new OutputCapture();

    private static ConfigurableApplicationContext server;
    private static Map<String, Object> props;

    @BeforeClass
    public static void beforeClass() {
        final String serverPort = System.getProperty("server.port") != null
                ? System.getProperty("server.port")
                : "54321";

        props = new HashMap<>();
        props.put("server.port", serverPort);

        setAdapterURLs();

        server = new SpringApplicationBuilder()
                .sources(Application.class, ContextConfiguration.class)
                .properties(ApplicationLoggingTest.getProps())
                .run();
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
        server.close();
    }

    private static void setAdapterURLs() {
        System.setProperty("REPORTER_CONSOLE_ENABLED", "true");
        System.setProperty("REPORTER_TIMEUNIT", "MILLISECONDS");
        System.setProperty("REPORTER_TIMEPERIOD", "100");
    }

    private static Map<String, Object> getProps() {
        return props;
    }

    @Test
    public void testConsoleLog() {
        System.out.println("CAPTURE: " + capture.toString());
        Assert.assertTrue(capture.toString().contains("-- Timers ----------------------------------------------------------------------"));
    }

}