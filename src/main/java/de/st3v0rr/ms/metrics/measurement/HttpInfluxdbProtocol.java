package de.st3v0rr.ms.metrics.measurement;

public class HttpInfluxdbProtocol {
    private final static String DEFAULT_HOST = "127.0.0.1";
    private final static int DEFAULT_PORT = 8086;

    private final String user;
    private final String password;
    private final String host;
    private final int port;
    private final String database;

    public HttpInfluxdbProtocol(String host, int port, String user, String password, String db) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = db;
    }

    public HttpInfluxdbProtocol(String host, int port) {
        this(host, port, null, null, null);
    }

    public HttpInfluxdbProtocol() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
