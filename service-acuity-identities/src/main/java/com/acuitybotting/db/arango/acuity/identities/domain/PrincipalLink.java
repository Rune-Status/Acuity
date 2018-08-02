package com.acuitybotting.db.arango.acuity.identities.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
@Document("PrincipalLink")
@Setter
@Getter
public class PrincipalLink {

    @Id
    private String id;

    private Principal principal1;
    private Principal principal2;
}
