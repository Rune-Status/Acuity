package com.acuitybotting.db.arangodb.repositories.pathing;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import org.springframework.stereotype.Service;

@Service
public class PathingRepository extends ArangoRepository<String> {

    protected PathingRepository(ArangoDbService arangoDbService) {
        super(String.class, arangoDbService);
        setDbName("Pathing-1");
    }

    @Override
    public String getCollectionName() {
        return null;
    }
}
