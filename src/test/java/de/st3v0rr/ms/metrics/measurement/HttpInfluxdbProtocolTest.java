package de.st3v0rr.ms.metrics.measurement;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpInfluxdbProtocolTest {

    private HttpInfluxdbProtocol httpInfluxdbProtocol;

    @Before
    public void setUp() {
        httpInfluxdbProtocol = new HttpInfluxdbProtocol("1.1.1.1", 80, "user", "secret", "customdb");
    }

    @Test
    public void testDefaultHost() {
        assertEquals("127.0.0.1", new HttpInfluxdbProtocol().getHost());
    }

    @Test
    public void testDefaultPort() {
        assertEquals(8086, new HttpInfluxdbProtocol().getPort());
    }

    @Test
    public void testCustomHost() {
        assertEquals("1.1.1.1", new HttpInfluxdbProtocol("1.1.1.1", 8086).getHost());
    }

    @Test
    public void testCustomPort() {
        assertEquals(80, new HttpInfluxdbProtocol("1.1.1.1", 80).getPort());
    }

    @Test
    public void testAuthUser() {
        assertEquals("user", httpInfluxdbProtocol.getUser());
    }

    @Test
    public void testAuthPassword() {
        assertEquals("secret", httpInfluxdbProtocol.getPassword());
    }

    @Test
    public void testDatabase() {
        assertEquals("customdb", httpInfluxdbProtocol.getDatabase());
    }

}
