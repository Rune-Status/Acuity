package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitSubDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
@Getter
@Setter
@ToString
public class ClientConnection extends RabbitSubDocument {

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
