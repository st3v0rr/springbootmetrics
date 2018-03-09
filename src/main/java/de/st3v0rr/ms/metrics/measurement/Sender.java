package de.st3v0rr.ms.metrics.measurement;

import org.influxdb.dto.Point;

import java.util.List;

public interface Sender {
	void send(List<Point> points);
}
