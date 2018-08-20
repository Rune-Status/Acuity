package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.client;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class RabbitClient {

    public static final String FUTURE_ID = "futureId";
    public static final String RESPONSE_ID = "responseId";
    public static final String RESPONSE_QUEUE = "responseQueue";

    public final Object CONFIRM_STATE_LOCK = new Object();

    private String connectionName;
    private String endpoint;
    private String virtualHost;
    private String username;
    private String password;
    private String port;

    private ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(1);

    private Gson gson = new Gson();

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;

    private Map<String, MessageFuture> messageFutures = new HashMap<>();

    private static Consumer<Throwable> throwableConsumer = throwable -> throwable.printStackTrace();
    private static Consumer<String> logConsumer = s -> System.out.println(s);

    private Collection<RabbitChannel> channels = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> scheduledFuture;

    public void auth(String endpoint, String port, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void connect(String connectionName) {
        if (scheduledFuture != null) throw new IllegalStateException("Client already connected.");

        this.connectionName = connectionName;
        factory.setHost(endpoint);
        factory.setPort(Integer.parseInt(port));
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setTopologyRecoveryEnabled(false);
        factory.setAutomaticRecoveryEnabled(false);
        if (virtualHost != null) factory.setVirtualHost(virtualHost);

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::confirmState, 0, 10, TimeUnit.SECONDS);
    }

    private boolean close() {
        synchronized (CONFIRM_STATE_LOCK){
            if (connection != null && connection.isOpen()) {
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
                        connection = factory.newConnection(connectionName);
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

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public Map<String, MessageFuture> getMessageFutures() {
        return messageFutures;
    }

    public Consumer<Throwable> getExceptionHandler() {
        return throwableConsumer;
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    public RabbitChannel openChannel() throws RuntimeException {
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

    public static void setLogConsumer(Consumer<String> logConsumer) {
        RabbitClient.logConsumer = logConsumer;
    }

    public static void setThrowableConsumer(Consumer<Throwable> throwableConsumer) {
        RabbitClient.throwableConsumer = throwableConsumer;
    }
}
