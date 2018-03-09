package de.st3v0rr.ms.metrics;

import com.codahale.metrics.annotation.Timed;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
public class DoSomeThing {

    @Timed
    public String doSomeThing() {
        return randomUUID().toString();
    }
}
