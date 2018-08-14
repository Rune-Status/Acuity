package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.client.RabbitClient;

import java.util.Objects;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface Messageable {

    default MessageBuilder buildResponse(Message message, String body) {
        return buildResponse(message, null, body);
    }

    default MessageBuilder buildResponse(Message message, String localQueue, String body) {
        String responseTopic = message.getAttributes().get(RabbitClient.RESPONSE_QUEUE);
        String responseId = message.getAttributes().get(RabbitClient.RESPONSE_ID);

        Objects.requireNonNull(responseTopic);
        Objects.requireNonNull(responseId);

        return buildMessage("", responseTopic, localQueue, responseId, body);
    }

    default MessageBuilder buildMessage(String targetExchange, String targetRouting, String body) {
        return buildMessage(targetExchange, targetRouting, null, body);
    }

    default MessageBuilder buildMessage(String targetExchange, String targetRouting, String localQueue, String body) {
        return buildMessage(targetExchange, targetRouting, localQueue, null, body);
    }

    default MessageBuilder buildMessage(String targetExchange, String targetRouting, String localQueue, String futureId, String body) {
        return createMessage().setTargetExchange(targetExchange).setTargetRouting(targetRouting).setLocalQueue(localQueue).setFutureId(futureId).setBody(body);
    }

    MessageBuilder createMessage();
}
