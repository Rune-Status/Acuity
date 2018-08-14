package com.acuitybotting.client.bot.control.domain;

public class AcuityConnectionConfig {

    private String connectionKey;
    private String masterKey;

    private String connectionId;

    public String getConnectionId() {
        return connectionId;
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public AcuityConnectionConfig setConnectionKey(String connectionKey) {
        this.connectionKey = connectionKey;
        return this;
    }

    public AcuityConnectionConfig setMasterKey(String masterKey) {
        this.masterKey = masterKey;
        return this;
    }

    public AcuityConnectionConfig setConnectionId(String connectionId) {
        this.connectionId = connectionId;
        return this;
    }
}