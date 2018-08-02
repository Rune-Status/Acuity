package com.acuitybotting.db.arango.acuity.identities.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.PrincipalLink;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
public interface PrincipalLinkRepository extends ArangoRepository<PrincipalLink> {

    @Query("FOR p IN PrincipalLink FILTER p.principal1.uid == @0 OR p.principal2.uid == @0 RETURN p")
    Set<PrincipalLink> findAllLinksContaining(String uid);
}
