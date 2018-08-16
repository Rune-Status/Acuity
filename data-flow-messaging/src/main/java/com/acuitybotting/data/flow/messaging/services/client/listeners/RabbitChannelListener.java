package com.acuitybotting.data.flow.messaging.services.client.listeners;


import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by Zachary Herridge on 8/13/2018.
 */
public interface RabbitChannelListener {

    void onConnect(RabbitChannel channel);

    void onDisconnect(RabbitChannel channel, ShutdownSignalException e);
}
