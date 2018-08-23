package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitSubDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Proxy extends RabbitSubDocument {

    private String host;
    private String port;
    private String username;
    private String encryptedPassword;
}
