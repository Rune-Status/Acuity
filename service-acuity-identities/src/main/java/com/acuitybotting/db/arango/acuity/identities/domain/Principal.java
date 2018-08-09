package com.acuitybotting.db.arango.acuity.identities.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
@Getter
@Setter
@ToString
public class Principal {

    private String type;
    private String uid;

    public static Principal of(String type, String uid){
        Principal principal = new Principal();
        principal.setType(type);
        principal.setUid(uid);
        return principal;
    }
}
