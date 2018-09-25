package com.acuitybotting.db.arangodb.repositories.pathing;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.query.AqlQuery;
import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WayPointRepository extends ArangoRepository<WayPoint> {

    protected WayPointRepository(ArangoDbService arangoDbService) {
        super(WayPoint.class, arangoDbService);
        setDbName("Pathing-1");
    }

    @Override
    public String getCollectionName() {
        return "WayPoint";
    }

    public Set<WayPoint> findNear(int x, int y, int plane){
        return findNear(x, y, plane, 10);
    }

    public Set<WayPoint> findNear(int x, int y, int plane, int limit){
        AqlQuery query = Aql.query("FOR n IN NEAR('WayPoint', @lat, @long, 100) \n" +
                "            FILTER n.plane == @plane\n" +
                "            LIMIT @limit\n" +
                "            RETURN n");

        query.withParameters(
                "lat", GeoUtil.rsToGeo(x),
                "long", GeoUtil.rsToGeo(y),
                "plane", plane,
                "limit", limit
        );

        return execute(query).stream().collect(Collectors.toSet());
    }
}
