package de.st3v0rr.ms.metrics;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationEndpointTest {

    @LocalServerPort
    int port;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test
    public void testHttpOkEndpoint_StatusOK() {
        ResponseEntity<String> stringResponseEntity = getStringResponseEntity("/http_ok200");
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
    }

    @Test
    public void testHttpError404Endpoint_StatusError404() {
        ResponseEntity<String> stringResponseEntity = getStringResponseEntity("/http_error404");
        Assert.assertEquals(HttpStatus.NOT_FOUND, stringResponseEntity.getStatusCode());
    }

    @Test
    public void testHttpError500Endpoint_StatusError500() {
        ResponseEntity<String> stringResponseEntity = getStringResponseEntity("/http_error500");
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());
    }

    private ResponseEntity<String> getStringResponseEntity(String endPoint) {
        return testRestTemplate.getForEntity(buildEndpointUrl() + endPoint, String.class);
    }

    private String buildEndpointUrl() {
        return "http://localhost:" + port + "/testservice";
    }

}