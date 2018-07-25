package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.management.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Getter
@ToString
public class RabbitConnection {

    private String node;
    private long connected_at;
    private String vhost;
    private String user;
    private String peer_host;
    private String host;
    private String state;
    private String name;
    private String user_provided_name;

}

