package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingChannelListener;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.*;


/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class RabbitChannel implements MessagingChannel, ShutdownListener {

    private final Object connectLock = new Object();

    private RabbitClient rabbitClient;
    private List<MessagingChannelListener> listeners = new CopyOnWriteArrayList<>();

    private Channel rabbitChannel;
    private long lastConnectionAttempt = 0;

    private ScheduledFuture<?> scheduledFuture;

    public RabbitChannel(RabbitClient rabbitClient) {
        this.rabbitClient = rabbitClient;
    }

    @Override
    public void connect() {
        if (scheduledFuture != null) throw new IllegalStateException("Client already connected.");
        scheduledFuture = rabbitClient.getScheduledExecutorService().scheduleAtFixedRate(this::checkConnection, 0, 20, TimeUnit.SECONDS);
    }

    private void checkConnection(){
        if (rabbitChannel != null && rabbitChannel.isOpen()) return;

        synchronized (connectLock){
            long now = System.currentTimeMillis();
            if ((now - lastConnectionAttempt) > TimeUnit.SECONDS.toMillis(9)){
                lastConnectionAttempt = now;

                if (rabbitChannel != null && rabbitChannel.isOpen()) return;

                try {
                    rabbitChannel = rabbitClient.getConnection().createChannel();
                    rabbitClient.getLog().accept("Channel opened.");
                    rabbitChannel.addShutdownListener(this);
                    rabbitChannel.basicQos(6);

                    for (MessagingChannelListener listener : listeners) {
                        try {
                            listener.onConnect(this);
                        }
                        catch (Throwable e){
                            rabbitClient.getExceptionHandler().accept(e);
                        }
                    }

                    return;
                } catch (Throwable e) {
                    rabbitClient.getExceptionHandler().accept(e);
                }
            }
        }

        rabbitClient.getLog().accept("Failed to open channel connection, waiting 20 seconds and trying again.");
    }

    @Override
    public MessagingQueue getQueue(String queue) {
        return new RabbitQueue(this, queue);
    }

    @Override
    public MessagingChannel close() throws MessagingException {
        try {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
            getChannel().close();
        } catch (IOException | TimeoutException e) {
            throw new MessagingException("Failed to close channel", e);
        }
        return this;
    }

    @Override
    public MessagingClient getClient() {
        return rabbitClient;
    }

    @Override
    public void acknowledge(Message message) throws MessagingException {
        try {
            Channel channel = getChannel();
            if (channel == null || !channel.isOpen()) throw new MessagingException("Not connected to RabbitMQ.");
            channel.basicAck(message.getRabbitTag(), false);
        } catch (Throwable e) {
            throw new MessagingException("Error during acknowledging message: " + message + ".", e);
        }
    }

    @Override
    public Future<MessageEvent> send(String targetExchange, String targetRouting, String localQueue, String futureId, String body) throws MessagingException {
        Channel channel = getChannel();
        if (channel == null || !channel.isOpen()) throw new MessagingException("Not connected to RabbitMQ.");

        rabbitClient.getLog().accept("Sending to exchange '" + targetExchange + "' with routing '" + targetRouting + "' body: " + body);

        Map<String, String> messageAttributes = new HashMap<>();
        String generatedId = null;
        if (futureId != null) messageAttributes.put(FUTURE_ID, futureId);

        MessageFuture future = null;
        if (localQueue != null) {
            generatedId = generateId();
            future = new MessageFuture();
            future.whenComplete((message, throwable) -> rabbitClient.getMessageFutures().remove(futureId));
            rabbitClient.getMessageFutures().put(generatedId, future);
            messageAttributes.put(RESPONSE_ID, generatedId);
            messageAttributes.put(RESPONSE_QUEUE, localQueue);
        }

        Message message = new Message();
        message.setId(generateId());
        message.setAttributes(messageAttributes);
        message.setBody(body);

        for (MessagingChannelListener listener : listeners) {
            try {
                listener.beforeMessageSend(this, message);
            } catch (Throwable e) {
                rabbitClient.getExceptionHandler().accept(e);
            }
        }

        try {
            channel.basicPublish(targetExchange, targetRouting, null, rabbitClient.getGson().toJson(message).getBytes());
            return future;
        } catch (Throwable e) {
            if (generatedId != null) rabbitClient.getMessageFutures().remove(generatedId);
            throw new MessagingException("Exception during message publish.", e);
        }
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public MessageFuture getMessageFuture(String id) {
        return rabbitClient.getMessageFutures().get(id);
    }

    @Override
    public List<MessagingChannelListener> getListeners() {
        return listeners;
    }

    public Channel getChannel() {
        return rabbitChannel;
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException shutdownEvent) {
        rabbitClient.getLog().accept("Channel shutdown complete. " + shutdownEvent);
        rabbitChannel = null;
        for (MessagingChannelListener listener : listeners) {
            try {
                listener.onShutdown(this, shutdownEvent.getCause());
            } catch (Throwable e) {
                rabbitClient.getExceptionHandler().accept(e);
            }
        }
    }
}
