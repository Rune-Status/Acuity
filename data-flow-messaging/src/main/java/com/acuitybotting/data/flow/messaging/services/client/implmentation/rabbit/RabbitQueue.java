package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingQueueListener;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class RabbitQueue implements MessagingQueue {

    private RabbitClient client;
    private RabbitChannel channel;
    private String queueName;
    private String consumeId;

    private DefaultConsumer defaultConsumer;

    private List<MessagingQueueListener> listeners = new CopyOnWriteArrayList<>();

    public RabbitQueue(RabbitChannel channel, String queueName) {
        this.channel = channel;
        this.client = (RabbitClient) channel.getClient();
        this.queueName = queueName;

        defaultConsumer = new DefaultConsumer(channel.getChannel()) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                onDelivery(consumerTag, envelope, properties, body);
            }
        };
    }

    @Override
    public MessagingQueue bind(String exchange, String routing) throws MessagingException {
        try {
            channel.getChannel().queueBind(queueName, exchange, routing);
            client.getLog().accept("Bound queue '" + queueName + "' to exchange '" + exchange + "' with routing key '" + routing + "'.");
        } catch (Throwable e) {
            throw new MessagingException("Exception during binding queue named '" + queueName + "' to exchange named '" + exchange + "' with routing key '" + routing + "'.", e);
        }
        return this;
    }

    @Override
    public MessagingQueue create() throws MessagingException {
        try {
            queueName = channel.getChannel().queueDeclare(queueName, false, true, true, null).getQueue();
            client.getLog().accept("Queue declared named '" + queueName + "'.");
        }
        catch (Throwable e) {
            throw new MessagingException("Exception during queue creation with name '" + queueName + "'.", e);
        }

        return this;
    }

    @Override
    public MessagingQueue consume(boolean autoAcknowledge) throws MessagingException {
        try {
            consumeId = channel.getChannel().basicConsume(queueName, autoAcknowledge, defaultConsumer);
            client.getLog().accept("Consuming queue named '" + queueName + "' with consume id '" + consumeId + "'.");
        } catch (Throwable e) {
            throw new MessagingException("Exception during consuming queue with name '" + queueName + "'.", e);
        }
        return this;
    }

    @Override
    public MessagingChannel getChannel() {
        return channel;
    }

    @Override
    public MessagingQueue withListener(MessagingQueueListener listener) {
        listeners.add(listener);
        return this;
    }

    private void onDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            Message message = null;

            if (body != null && body.length > 0)
                message = client.getGson().fromJson(new String(body), Message.class);
            if (message == null) message = new Message();
            if (message.getAttributes() == null) message.setAttributes(new HashMap<>());

            message.getAttributes().put("envelope.exchange", envelope.getExchange());
            message.getAttributes().put("envelope.routing", envelope.getRoutingKey());

            message.setRabbitTag(envelope.getDeliveryTag());

            if (properties.getHeaders() != null) {
                for (Map.Entry<String, Object> header : properties.getHeaders().entrySet()) {
                    message.getAttributes().put("header." + header.getKey(), String.valueOf(header.getValue()));
                }
            }

            if (properties.getReplyTo() != null)
                message.getAttributes().put("properties.reply-to", properties.getReplyTo());
            if (properties.getCorrelationId() != null)
                message.getAttributes().put("properties.correlation-id", properties.getCorrelationId());


            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setMessage(message);
            messageEvent.setQueue(this);

            String futureId = message.getAttributes().get(MessagingClient.FUTURE_ID);
            if (futureId != null) {
                MessageFuture messageFuture = client.getMessageFutures().get(futureId);
                if (messageFuture != null) {
                    messageFuture.complete(messageEvent);
                }
            }

            for (MessagingQueueListener listener : listeners) {
                try {
                    listener.onMessage(messageEvent);
                }
                catch (Throwable e){
                    client.getExceptionHandler().accept(e);
                }
            }

        } catch (Throwable e) {
            client.getExceptionHandler().accept(e);
        }
    }
}
