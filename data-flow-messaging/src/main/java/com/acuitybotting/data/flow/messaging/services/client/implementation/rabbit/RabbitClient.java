package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Slf4j
public class RabbitClient implements MessagingClient {

    public final Object CONFIRM_STATE_LOCK = new Object();

    private String rabbitId;
    private String endpoint;
    private String virtualHost;
    private String username;
    private String password;

    private ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(1);

    private Gson gson = new Gson();

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;

    private Map<String, MessageFuture> messageFutures = new HashMap<>();

    private Consumer<Throwable> throwableConsumer = throwable -> log.error("Error from Rabbit.", throwable);
    private Consumer<String> logConsumer = s -> log.info(s);

    private Collection<RabbitChannel> channels = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void auth(String endpoint, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect(String connectionId) {
        if (scheduledFuture != null) throw new IllegalStateException("Client already connected.");

        rabbitId = connectionId;
        factory.setHost(endpoint);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setTopologyRecoveryEnabled(false);
        factory.setAutomaticRecoveryEnabled(false);
        if (virtualHost != null) factory.setVirtualHost(virtualHost);

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::confirmState, 0, 10, TimeUnit.SECONDS);
    }

    private boolean close() {
        synchronized (CONFIRM_STATE_LOCK){
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable e) {
                    getExceptionHandler().accept(e);
                }
            }

            if (connection != null && !connection.isOpen()) connection = null;
            return connection == null;
        }
    }

    private void confirmState() {
        synchronized (CONFIRM_STATE_LOCK){
            if (connection == null || !connection.isOpen()){
                try {
                    if (close()) {
                        connection = factory.newConnection(rabbitId);
                        if (!connection.isOpen()) getLog().accept("Failed to open RabbitMQ connection, waiting 10 seconds and trying again.");
                    }
                } catch (Throwable e) {
                    getExceptionHandler().accept(e);
                }
            }

            if (connection != null && connection.isOpen()){
                for (RabbitChannel channel : channels) {
                    try {
                        channel.confirmState();
                    } catch (Throwable e) {
                        getExceptionHandler().accept(e);
                    }
                }
            }
        }
    }

    public RabbitClient setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
        return this;
    }

    public Collection<RabbitChannel> getChannels() {
        return channels;
    }

    public String getRabbitId() {
        return rabbitId;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public Map<String, MessageFuture> getMessageFutures() {
        return messageFutures;
    }

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return throwableConsumer;
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    @Override
    public MessagingChannel openChannel() throws RuntimeException {
        RabbitChannel rabbitChannel = new RabbitChannel(this);
        channels.add(rabbitChannel);
        confirmState();
        return rabbitChannel;
    }

    public Gson getGson() {
        return gson;
    }

    public Consumer<String> getLog() {
        return logConsumer;
    }

    public Connection getConnection() {
        return connection;
    }
}
