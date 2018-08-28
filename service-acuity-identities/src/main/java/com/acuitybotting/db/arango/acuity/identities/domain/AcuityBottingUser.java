package com.acuitybotting.db.arango.acuity.identities.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Getter
@Setter
@ToString
public class AcuityBottingUser {

    private String _key;

    private String revision;

    private String email;
    private String displayName;
    private String profileImgUrl;

    private String passwordHash;
    private String masterKey;

    private String connectionKey;

    private Set<Principal> linkedPrincipals;
}
