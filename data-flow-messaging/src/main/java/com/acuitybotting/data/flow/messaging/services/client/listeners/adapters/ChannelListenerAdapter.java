package com.acuitybotting.data.flow.messaging.services.client.listeners.adapters;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingChannelListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public class ChannelListenerAdapter implements MessagingChannelListener {
    @Override
    public void onConnect(MessagingChannel channel) {

    }

    @Override
    public void onDisconnect(MessagingChannel channel, ShutdownSignalException e) {

    }
}
