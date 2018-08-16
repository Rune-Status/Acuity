package com.acuitybotting.client.bot.control.domain;


import lombok.Getter;

/**
 * Created by Zachary Herridge on 8/16/2018.
 */
@Getter
public class ClientConfiguration {

    private String accountLogin;
    private String accountEncryptedPassword;

    private String proxyHost;
    private String proxyPort;
    private String proxyUsername;
    private String proxyEncryptedPassword;


    private boolean scriptLocal;
    private String scriptSelector;
    private String scriptArgs;
}
