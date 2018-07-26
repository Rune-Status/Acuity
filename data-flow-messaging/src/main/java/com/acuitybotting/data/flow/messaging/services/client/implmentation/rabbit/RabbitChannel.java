package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
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

    public RabbitChannel(RabbitClient rabbitClient) {
        this.rabbitClient = rabbitClient;
    }

    @Override
    public void connect() {
        rabbitClient.getExecutor().execute(this::doConnect);
    }

    private void doConnect(){
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

        rabbitClient.getLog().accept("Failed to open channel connection, waiting 10 seconds and trying again.");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            rabbitClient.getExceptionHandler().accept(e);
        }

        rabbitClient.getExecutor().execute(this::doConnect);
    }

    @Override
    public MessagingQueue getQueue(String queue) {
        return new RabbitQueue(this, queue);
    }

    @Override
    public MessagingChannel close() throws RuntimeException {
        try {
            getChannel().close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to close channel", e);
        }
        return this;
    }

    @Override
    public MessagingClient getClient() {
        return rabbitClient;
    }

    @Override
    public void acknowledge(Message message) throws RuntimeException {
        try {
            Channel channel = getChannel();
            if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");
            channel.basicAck(message.getRabbitTag(), false);
        } catch (Throwable e) {
            throw new RuntimeException("Error during acknowledging message: " + message + ".", e);
        }
    }

    @Override
    public Future<MessageEvent> send(String targetExchange, String targetRouting, String localQueue, String futureId, String body) throws RuntimeException {
        Channel channel = getChannel();
        if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");

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
        } catch (IOException e) {
            if (generatedId != null) rabbitClient.getMessageFutures().remove(generatedId);
            throw new RuntimeException("Exception during message publish.", e);
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
        for (MessagingChannelListener listener : listeners) {
            try {
                listener.onShutdown(this, shutdownEvent.getCause());
            } catch (Throwable e) {
                rabbitClient.getExceptionHandler().accept(e);
            }
        }
    }
}
