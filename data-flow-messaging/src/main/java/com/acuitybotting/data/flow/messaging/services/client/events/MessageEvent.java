package com.acuitybotting.data.flow.messaging.services.client.events;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class MessageEvent {

    private MessagingQueue queue;
    private Message message;

    public MessageEvent(MessagingQueue queue, Message message) {
        this.queue = queue;
        this.message = message;
    }

    public MessagingQueue getQueue() {
        return queue;
    }

    public Message getMessage() {
        return message;
    }
}
