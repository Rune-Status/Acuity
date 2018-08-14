package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management;

import com.acuitybotting.common.utils.HttpUtil;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.domain.RabbitConnection;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Slf4j
public class RabbitManagement {

    private static Cache<String, List<RabbitConnection>> connections = CacheBuilder.newBuilder()
            .expireAfterWrite(25, TimeUnit.SECONDS)
            .build();


    public static void loadAll(String username, String password) throws Exception {
        String json = HttpUtil.get(HttpUtil.addBasicAuthHeader(new HashMap<>(), username, password), "http://" + "nodes-1.admin-acuitybotting.com" + ":" + "31456" + "/api/connections", null);
        RabbitConnection[] rabbitConnections = new Gson().fromJson(json, RabbitConnection[].class);
        Map<String, List<RabbitConnection>> collect = Arrays.stream(rabbitConnections).collect(Collectors.groupingBy(RabbitConnection::getUser));
        collect.forEach((key, value) -> connections.put(key, value));
    }

    public static Map<String, List<RabbitConnection>> getConnections() {
        return connections.asMap();
    }

    public static List<RabbitConnection> getConnectionsByUser(String rabbitUsername) {
        return Optional.ofNullable(connections.getIfPresent(rabbitUsername)).orElse(Collections.emptyList());
    }
}

