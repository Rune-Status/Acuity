package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingQueueListener;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public interface MessagingQueue {

    MessagingQueue bind(String exchange, String routing) throws MessagingException;

    MessagingQueue create() throws MessagingException;

    MessagingQueue consume(boolean autoAcknowledge) throws MessagingException;

    MessagingChannel getChannel();

    MessagingQueue withListener(MessagingQueueListener listener);

    String getName();
}
