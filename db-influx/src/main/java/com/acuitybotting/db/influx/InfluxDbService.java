package com.acuitybotting.db.influx;

import com.acuitybotting.common.utils.ExecutorUtil;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

@Service
public class InfluxDbService {

    @Value("${influx.username}")
    private String influxUsername;
    @Value("${influx.password}")
    private String influxPassword;
    private AtomicReference<InfluxDB> influxDB = new AtomicReference<>();

    private ExecutorService executorService = ExecutorUtil.newExecutorPool(1);

    private InfluxDB connect(String host) {
        return InfluxDBFactory.connect(host, influxUsername, influxPassword).setDatabase("acuitybotting-prod-1");
    }

    public InfluxDB getInfluxDB() {
        influxDB.compareAndSet(null, connect("http://159.69.47.70:30390"));
        return influxDB.get();
    }

    public void writeAsync(Point build) {
        executorService.submit(() -> getInfluxDB().write(build));
    }
}
