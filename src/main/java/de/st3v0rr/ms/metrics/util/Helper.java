package de.st3v0rr.ms.metrics.util;

import de.st3v0rr.ms.metrics.measurement.HttpInfluxdbProtocol;

public class Helper {

    public static String buildUrl(HttpInfluxdbProtocol protocol) {
        return "http://" + protocol.getHost() + ":" + protocol.getPort();
    }

}
