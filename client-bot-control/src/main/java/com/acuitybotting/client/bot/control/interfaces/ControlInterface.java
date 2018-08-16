package com.acuitybotting.client.bot.control.interfaces;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public interface ControlInterface {

    void applyAccount(String email, String password);

    void applyProxy(String asString, String asString1, String proxyUsername, String decrypt);

    void applyScript(String scriptSelector, boolean scriptLocal);
}
