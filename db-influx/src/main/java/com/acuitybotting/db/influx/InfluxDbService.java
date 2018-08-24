package com.acuitybotting.db.influx;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.db.influx.domain.query.QueryResult;
import com.acuitybotting.db.influx.domain.write.Point;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

@Service
public class InfluxDbService {

    @Value("${influx.username}")
    private String influxUsername;
    @Value("${influx.password}")
    private String influxPassword;

    private String host = "http://88.198.201.19:30103";

    private OkHttpClient client = new OkHttpClient();

    private ExecutorService executorService = ExecutorUtil.newExecutorPool(5);

    public void writeAsync(Point build) {
        executorService.submit(() -> write("acuitybotting-prod-1", build));
    }

    public QueryResult query(String db, String query){
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(host + "/query")).newBuilder();
        urlBuilder.addEncodedQueryParameter("u", influxUsername);
        urlBuilder.addEncodedQueryParameter("p", influxPassword);
        urlBuilder.addEncodedQueryParameter("db", db);
        urlBuilder.addEncodedQueryParameter("epoch", "ms");
        urlBuilder.addQueryParameter("q", query);

        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            System.out.println(request);
            Call call = client.newCall(request);
            Response response = call.execute();

            ResponseBody body = response.body();
            System.out.println(body);
            if (body != null){
                String string = body.string();
                System.out.println(string);
                return new Gson().fromJson(string, QueryResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void write(String db, Point point) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(host + "/write")).newBuilder();
        urlBuilder.addEncodedQueryParameter("u", influxUsername);
        urlBuilder.addEncodedQueryParameter("p", influxPassword);
        urlBuilder.addEncodedQueryParameter("db", db);

        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), point.toLineProtocol()))
                .build();

        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
