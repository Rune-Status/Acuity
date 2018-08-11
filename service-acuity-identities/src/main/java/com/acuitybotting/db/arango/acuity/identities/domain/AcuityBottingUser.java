package com.acuitybotting.db.arango.acuity.identities.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Rev;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Getter
@Setter
@ToString
@Document("AcuityBottingUser")
public class AcuityBottingUser {

    @Id
    private String id;

    @Rev
    private String revision;

    private String principalId;

    private String email;
    private String displayName;

    private String passwordHash;
    private String masterKey;

    private Set<Principal> linkedPrincipals;

}
