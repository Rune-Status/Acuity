package com.acuitybotting.db.arango.acuity.script.repository.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import lombok.Data;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
@Data
@Document("ScriptAuth")
public class ScriptAuth {

    public static final int PAID = 1;
    public static final int PRIVATE_SCRIPT = 2;

    private long creationTime;
    private long expirationTime;

    private int authType;

    @Ref
    private Script script;

    public boolean isActive(){
        return expirationTime == 0 || System.currentTimeMillis() < expirationTime;
    }
}
