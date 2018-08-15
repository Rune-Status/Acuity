package com.acuitybotting.common.utils.connection_configuration.domain;

public class ConnectionConfiguration {

    private String connectionKey;
    private String connectionId;

    private String masterKey;

    public String getConnectionId() {
        return connectionId;
    }

    public ConnectionConfiguration setConnectionId(String connectionId) {
        this.connectionId = connectionId;
        return this;
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public ConnectionConfiguration setConnectionKey(String connectionKey) {
        this.connectionKey = connectionKey;
        return this;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public ConnectionConfiguration setMasterKey(String masterKey) {
        this.masterKey = masterKey;
        return this;
    }
}