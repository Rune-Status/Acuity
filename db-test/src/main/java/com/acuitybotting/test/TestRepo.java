package com.acuitybotting.test;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import org.springframework.stereotype.Service;

@Service
public class TestRepo extends ArangoRepository<String> {

    protected TestRepo(ArangoDbService arangoDbService) {
        super(String.class, arangoDbService);
    }

    @Override
    public String getCollectionName() {
        return "Test";
    }
}
