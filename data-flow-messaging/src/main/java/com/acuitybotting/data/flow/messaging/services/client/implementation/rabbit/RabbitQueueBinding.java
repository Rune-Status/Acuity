package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
public class RabbitQueueBinding {

    private RabbitQueue rabbitQueue;
    private String exchange;
    private String routing;
    private String consumeId;

    public RabbitQueue getRabbitQueue() {
        return rabbitQueue;
    }

    public RabbitQueueBinding setRabbitQueue(RabbitQueue rabbitQueue) {
        this.rabbitQueue = rabbitQueue;
        return this;
    }

    public String getExchange() {
        return exchange;
    }

    public RabbitQueueBinding setExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public String getRouting() {
        return routing;
    }

    public RabbitQueueBinding setRouting(String routing) {
        this.routing = routing;
        return this;
    }

    public String getConsumeId() {
        return consumeId;
    }

    public RabbitQueueBinding setConsumeId(String consumeId) {
        this.consumeId = consumeId;
        return this;
    }
}
