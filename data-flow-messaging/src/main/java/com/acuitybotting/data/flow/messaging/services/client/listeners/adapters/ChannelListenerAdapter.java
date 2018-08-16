package com.acuitybotting.data.flow.messaging.services.client.listeners.adapters;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.listeners.RabbitChannelListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public class ChannelListenerAdapter implements RabbitChannelListener {
    @Override
    public void onConnect(RabbitChannel channel) {

    }

    @Override
    public void onDisconnect(RabbitChannel channel, ShutdownSignalException e) {

    }
}
