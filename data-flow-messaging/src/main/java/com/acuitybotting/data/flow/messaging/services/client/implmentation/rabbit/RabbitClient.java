package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingClientListener;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Slf4j
public class RabbitClient implements MessagingClient {

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
    private List<MessagingClientListener> listeners = new CopyOnWriteArrayList<>();

    private Consumer<Throwable> throwableConsumer = throwable -> log.error("Error from Rabbit.", throwable);
    private Consumer<String> logConsumer = s -> log.info(s);

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

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::checkConnection, 0, 10, TimeUnit.SECONDS);
    }

    private boolean close() {
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

    private void checkConnection() {
        if (connection != null && connection.isOpen()) return;

        try {
            if (close()) {
                connection = factory.newConnection(rabbitId);
                if (connection.isOpen()) {
                    connection.addShutdownListener(e -> {
                        for (MessagingClientListener listener : listeners) {
                            try {
                                listener.onShutdown(this);
                            } catch (Throwable e1) {
                                getExceptionHandler().accept(e1);
                            }
                        }
                    });
                    getLog().accept("RabbitMq connection opened.");
                    for (MessagingClientListener listener : listeners) {
                        try {
                            listener.onConnect(this);
                        } catch (Throwable e) {
                            getExceptionHandler().accept(e);
                        }
                    }
                    return;
                }
            }
        } catch (Throwable e) {
            getExceptionHandler().accept(e);
        }

        getLog().accept("Failed to open RabbitMQ connection, waiting 10 seconds and trying again.");
    }

    public RabbitClient setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
        return this;
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
    public List<MessagingClientListener> getListeners() {
        return listeners;
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
    public MessagingChannel createChannel() throws RuntimeException {
        return new RabbitChannel(this);
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
