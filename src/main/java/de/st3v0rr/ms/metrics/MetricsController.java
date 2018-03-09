package de.st3v0rr.ms.metrics;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testservice")
public class MetricsController {

    @Timed
    @GetMapping(path = "/http_ok200")
    public ResponseEntity<String> getHttpOk() {
        double random = Math.random() * 1000;
        try {
            Thread.sleep((long) random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = "Slept " + random + " ms";
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @Timed
    @GetMapping(path = "/http_error404")
    public ResponseEntity<String> getHttpError404() {
        return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
    }

    @Timed
    @GetMapping(path = "/http_error500")
    public ResponseEntity<String> getHttpError500() {
        return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Timed
    @GetMapping(path = "/dosomething")
    public ResponseEntity<String> doSomeThing() {
        DoSomeThing doSomeThing = new DoSomeThing();
        return new ResponseEntity<>(doSomeThing.doSomeThing(), HttpStatus.OK);
    }

    @GetMapping(path = {"/ready", "/alive"})
    public HttpEntity<Void> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = {"/version", "/ping"}, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        return String.format("Version: %s", extractVersion());
    }

    private String extractVersion() {
        Package p = MetricsController.class.getPackage();
        return p != null ? p.getImplementationVersion() : "";
    }

}