package com.acuitybotting.data.flow.messaging.services.events;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.queue.RabbitQueue;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public class MessageEvent {

    private RabbitQueue queue;
    private Message message;

    public RabbitQueue getQueue() {
        return queue;
    }

    public MessageEvent setQueue(RabbitQueue queue) {
        this.queue = queue;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public MessageEvent setMessage(Message message) {
        this.message = message;
        return this;
    }

    public String getRouting(){
        return String.valueOf(message.getAttributes().getOrDefault("envelope.routing", ""));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageEvent{");
        sb.append("queue=").append(queue);
        sb.append(", message=").append(message);
        sb.append('}');
        return sb.toString();
    }
}
