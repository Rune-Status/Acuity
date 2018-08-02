package com.acuitybotting.db.arango.acuity.identities.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.PrincipalLink;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
public interface PrincipalLinkRepository extends ArangoRepository<PrincipalLink> {
}
