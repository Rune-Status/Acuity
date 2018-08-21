package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.queue.RabbitQueue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public class RabbitChannelPool {

    private final RabbitHub hub;
    private Set<RabbitChannel> pool = new HashSet<>();

    public RabbitChannelPool(RabbitHub hub, int poolSize, Consumer<RabbitChannel> callback) {
        this.hub = hub;
        for (int i = 0; i < poolSize; i++) {
            RabbitChannel RabbitChannel = hub.getClient().openChannel();
            pool.add(RabbitChannel);
            if (callback != null) callback.accept(RabbitChannel);
        }
    }

    public RabbitQueue createQueue(String queue, boolean create) {
        return new RabbitQueue(this, queue).setCreateQueue(create);
    }

    public RabbitHub getHub() {
        return hub;
    }

    public Set<RabbitChannel> getPool() {
        return pool;
    }

    public RabbitChannel getChannel(){
        return pool.stream().filter(RabbitChannel::isConnected).findAny().orElse(pool.stream().findAny().orElse(null));
    }
}
