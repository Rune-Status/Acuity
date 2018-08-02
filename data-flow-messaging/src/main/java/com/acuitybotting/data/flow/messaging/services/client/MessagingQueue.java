package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitQueue;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingQueueListener;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public interface MessagingQueue {

    MessagingQueue bind(String exchange, String routing) throws MessagingException;

    MessagingChannel getChannel();

    MessagingQueue withListener(MessagingQueueListener listener);

    String getName();

    RabbitQueue connect();
}
