package com.acuitybotting.client.bot.control.interfaces;

import com.google.gson.JsonObject;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public interface StateInterface {

    JsonObject buildClientState();

    JsonObject buildPlayerState();
}
