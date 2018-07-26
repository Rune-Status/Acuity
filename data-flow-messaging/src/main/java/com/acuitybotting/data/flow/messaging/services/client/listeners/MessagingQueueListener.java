package com.acuitybotting.data.flow.messaging.services.client.listeners;

import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public interface MessagingQueueListener {

    void onMessage(MessageEvent messageEvent);

}
