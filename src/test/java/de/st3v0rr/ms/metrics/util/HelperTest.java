package de.st3v0rr.ms.metrics.util;

import de.st3v0rr.ms.metrics.measurement.HttpInfluxdbProtocol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelperTest {

    @Test
    public void testUrl() {
        assertEquals("http://127.0.0.1:8086", Helper.buildUrl(new HttpInfluxdbProtocol()));
    }

}
