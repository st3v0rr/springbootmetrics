package de.st3v0rr.ms.metrics;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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