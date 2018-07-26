package com.acuitybotting.data.flow.messaging.services.client.exceptions;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class MessagingException extends Exception {

    public MessagingException(String message, Throwable e) {
        super(message, e);
    }

    public MessagingException(String message) {
        super(message);
    }
}
