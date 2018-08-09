package com.acuitybotting.db.arango.acuity.identities.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Getter
@Setter
@ToString
@Document("AcuityBottingUser")
public class AcuityBottingUser {

    private String email;
    private String displayName;
    private String passwordHash;
}
