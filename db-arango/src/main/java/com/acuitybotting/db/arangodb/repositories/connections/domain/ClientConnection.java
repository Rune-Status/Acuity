package com.acuitybotting.db.arangodb.repositories.connections.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
@Getter
@Setter
@ToString
public class ClientConnection {

    private boolean connected;
    private ClientConnectionState state;

    @Getter
    @Setter
    @ToString
    public static class ClientConnectionState {

        private String externalPrincipalId;
        private String scriptSelector;
        private Number version;
        private boolean loggedIn;
        private boolean localScript;
        private boolean scriptPaused;
    }
}
