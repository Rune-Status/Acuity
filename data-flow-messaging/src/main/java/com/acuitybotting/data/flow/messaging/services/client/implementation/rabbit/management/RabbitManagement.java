package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management;

import com.acuitybotting.common.utils.HttpUtil;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.domain.RabbitConnection;

import com.acuitybotting.data.flow.messaging.services.identity.RabbitUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Slf4j
public class RabbitManagement {

    private static Map<String, Map<String, List<RabbitConnection>>> collect = new HashMap<>();

    public static void loadAll(String username, String password) throws Exception {
        String json = HttpUtil.get(HttpUtil.addBasicAuthHeader(new HashMap<>(), username, password), "http://" + "nodes-1.admin-acuitybotting.com" + ":" + "31456" + "/api/connections", null);
        RabbitConnection[] rabbitConnections = new Gson().fromJson(json, RabbitConnection[].class);
        collect = Arrays.stream(rabbitConnections).collect(
                Collectors.groupingBy(RabbitConnection::getUser,
                        Collectors.groupingBy(t -> RabbitUtil.connectionNameToType(t.getUser_provided_name()))));
    }

    public static Map<String, Map<String, List<RabbitConnection>>> getConnections() {
        return collect;
    }

    public static Map<String, List<RabbitConnection>> getConnectionsByUser(String rabbitUsername) {
        return collect.getOrDefault(rabbitUsername, Collections.emptyMap());
    }
}

