package com.acuitybotting.data.flow.messaging.services.client.implementation.basic;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
public class BasicMessageHub {

    private MessagingClient client;
    private MessagingChannel channel;

    public BasicMessageHub(MessagingClient client) {
        this.client = client;
        this.channel = client.openChannel();
    }
}
