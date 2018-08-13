package com.acuitybotting.data.flow.messaging.services.client.listeners;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public interface MessagingChannelListener {

    void onConnect(MessagingChannel channel);

    void onDisconnect(MessagingChannel channel, ShutdownSignalException e);
}
