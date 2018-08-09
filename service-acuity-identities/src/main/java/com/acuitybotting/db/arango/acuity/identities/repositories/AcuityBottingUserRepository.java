package com.acuitybotting.db.arango.acuity.identities.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
public interface AcuityBottingUserRepository extends ArangoRepository<AcuityBottingUser> {

    Optional<AcuityBottingUser> findByEmail(String email);

}
