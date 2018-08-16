package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class MessageBuilder {

    private RabbitChannel channel;

    private String targetExchange;
    private String targetRouting;
    private String localQueue;
    private String futureId;
    private String body;
    private Map<String, String> messageAttributes = new HashMap<>();

    public MessageBuilder(RabbitChannel channel) {
        this.channel = channel;
    }

    public Future<MessageEvent> send() throws MessagingException {
        return channel.send(this);
    }

    public MessageBuilder setAttribute(String key, String value){
        messageAttributes.put(key, value);
        return this;
    }

    public MessageBuilder setMessageAttributes(Map<String, String> messageAttributes) {
        this.messageAttributes = messageAttributes;
        return this;
    }

    public RabbitChannel getChannel() {
        return channel;
    }

    public MessageBuilder setChannel(RabbitChannel channel) {
        this.channel = channel;
        return this;
    }

    public String getTargetExchange() {
        return targetExchange;
    }

    public MessageBuilder setTargetExchange(String targetExchange) {
        this.targetExchange = targetExchange;
        return this;
    }

    public String getTargetRouting() {
        return targetRouting;
    }

    public MessageBuilder setTargetRouting(String targetRouting) {
        this.targetRouting = targetRouting;
        return this;
    }

    public String getLocalQueue() {
        return localQueue;
    }

    public MessageBuilder setLocalQueue(String localQueue) {
        this.localQueue = localQueue;
        return this;
    }

    public String getFutureId() {
        return futureId;
    }

    public MessageBuilder setFutureId(String futureId) {
        this.futureId = futureId;
        return this;
    }

    public String getBody() {
        return body;
    }

    public MessageBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getMessageAttributes() {
        return messageAttributes;
    }
}
