package com.acuitybotting.db.arangodb.repositories.pathing;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPointConnection;
import org.springframework.stereotype.Service;

@Service
public class WayPointConnectionRepository extends ArangoRepository<WayPointConnection> {

    protected WayPointConnectionRepository(ArangoDbService arangoDbService) {
        super(WayPointConnection.class, arangoDbService);
        setDbName("Pathing-1");
    }

    @Override
    public String getCollectionName() {
        return "WayPointConnection";
    }
}