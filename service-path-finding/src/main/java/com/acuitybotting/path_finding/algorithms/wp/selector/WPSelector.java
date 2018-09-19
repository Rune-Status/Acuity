package com.acuitybotting.path_finding.algorithms.wp.selector;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.wp.utils.GeoUtil;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
public class WPSelector implements CommandLineRunner {

    private Location lower;
    private Location upper;
    private PathFindingSupplier pathSupplier;

    private final WayPointRepository repository;

    public WPSelector(WayPointRepository repository) {
        this.repository = repository;
    }

    public void init(Location lower, Location upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public void run(String... args) throws Exception {
        init(null, null);

        for (int i = 1; i < 5; i++) {
            WayPoint wayPoint = new WayPoint(i * 100, i * 100, 1);
            wayPoint.setLatitude(GeoUtil.rsToGeo(wayPoint.getX()));
            wayPoint.setLongitude(GeoUtil.rsToGeo(wayPoint.getY()));
            repository.execute(Aql.insert(wayPoint));
        }
    }
}
