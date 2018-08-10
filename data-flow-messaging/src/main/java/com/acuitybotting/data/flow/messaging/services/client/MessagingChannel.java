package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;

import java.util.Objects;
import java.util.concurrent.Future;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_ID;
import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingChannel {

    MessagingQueue createQueue(String queue, boolean create);

    MessagingChannel close() throws MessagingException;

    MessagingClient getClient();

    void acknowledge(Message message) throws MessagingException;

    default MessageBuilder buildResponse(Message message, String body) {
        return buildResponse(message, null, body);
    }

    default MessageBuilder buildResponse(Message message, String localQueue, String body) {
        String responseTopic = message.getAttributes().get(RESPONSE_QUEUE);
        String responseId = message.getAttributes().get(RESPONSE_ID);

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

    default MessageBuilder createMessage(){
        return new MessageBuilder(this);
    }

    Future<MessageEvent> send(MessageBuilder messageBuilder) throws MessagingException;

    MessageFuture getMessageFuture(String id);
}
