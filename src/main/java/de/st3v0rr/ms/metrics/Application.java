package de.st3v0rr.ms.metrics;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMetrics
@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
