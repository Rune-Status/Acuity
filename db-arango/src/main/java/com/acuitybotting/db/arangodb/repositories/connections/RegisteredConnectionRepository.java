package com.acuitybotting.db.arangodb.repositories.connections;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import org.springframework.stereotype.Service;

@Service
public class RegisteredConnectionRepository extends ArangoRepository<String> {

    protected RegisteredConnectionRepository(ArangoDbService arangoDbService) {
        super(String.class, arangoDbService);
    }

    @Override
    public String getCollectionName() {
        return "registered-connections";
    }
}
