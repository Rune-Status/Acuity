package com.acuitybotting.client.bot.control;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public class AcuityHub {

    private static RabbitHub rabbitHub = new RabbitHub();

    public static void start(){
        String acuityConfig = System.getProperty("acuityConfig");





    }

    public static RabbitHub getRabbitHub() {
        return rabbitHub;
    }
}
