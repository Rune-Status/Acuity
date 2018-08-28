package com.acuitybotting.db.arangodb.repositories.acuity.principal;

import com.acuitybotting.db.arangodb.repositories.acuity.principal.domain.AcuityBottingUser;
import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Service
public class AcuityBottingUserRepository extends ArangoRepository<AcuityBottingUser> {

    protected AcuityBottingUserRepository(ArangoDbService arangoDbService) {
        super(AcuityBottingUser.class, arangoDbService);
    }

    @Override
    public String getCollectionName() {
        return "AcuityBottingUser";
    }

    public Optional<AcuityBottingUser> findByEmail(String email) {
        return findByFields("email", email).findAny();
    }
}
