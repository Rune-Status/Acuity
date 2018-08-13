package com.acuitybotting.data.flow.messaging.services.client.utils;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public class RabbitChannelPool {

    private Set<RabbitChannel> pool = new HashSet<>();

    public RabbitChannelPool(RabbitHub hub, int poolSize, Consumer<MessagingChannel> callback) {
        for (int i = 0; i < poolSize; i++) {
            MessagingChannel messagingChannel = hub.getRabbitClient().openChannel();
            if (callback != null) callback.accept(messagingChannel);
        }
    }

    public Set<RabbitChannel> getPool() {
        return pool;
    }

    public RabbitChannel getChannel(){
        return pool.stream().findAny().orElse(null);
    }
}
