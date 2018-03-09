package de.st3v0rr.ms.metrics.measurement;

import de.st3v0rr.ms.metrics.util.Helper;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SenderImpl implements Sender {

    private final HttpInfluxdbProtocol protocol;

    public SenderImpl(HttpInfluxdbProtocol protocol) {
        this.protocol = protocol;
    }

    public void send(List<Point> points) {
        String url = Helper.buildUrl(protocol);
        InfluxDB influxDB;
        if (protocol.getUser() == null && protocol.getPassword() == null) {
            influxDB = InfluxDBFactory.connect(url);
        } else {
            influxDB = InfluxDBFactory.connect(url, protocol.getUser(), protocol.getPassword());
        }

        influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
        influxDB.enableGzip();
        BatchPoints batchPoints = BatchPoints
                .database(protocol.getDatabase())
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        for (Point point : points) {
            batchPoints.point(point);
        }
        influxDB.write(batchPoints);
        influxDB.close();
    }

}
