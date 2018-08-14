package com.acuitybotting.client.bot.control.interfaces;

import java.util.Map;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public interface StateInterface {

    Map<String, Object> buildClientState();

    Map<String, Object> buildPlayerState();

}
