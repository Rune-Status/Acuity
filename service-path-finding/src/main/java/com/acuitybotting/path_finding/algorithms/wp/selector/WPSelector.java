package com.acuitybotting.path_finding.algorithms.wp.selector;

import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointConnectionRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Setter
@Getter
@Service
@Slf4j
public class WPSelector implements CommandLineRunner {

    private Location lower;
    private Location upper;
    private PathFindingSupplier pathSupplier;

    private final WayPointRepository wpRepository;
    private final WayPointConnectionRepository connectionRepository;
    private final HpaPathFindingService hpaPathFindingService;

    public WPSelector(WayPointRepository wpRepository, WayPointConnectionRepository connectionRepository, HpaPathFindingService hpaPathFindingService) {
        this.wpRepository = wpRepository;
        this.connectionRepository = connectionRepository;
        this.hpaPathFindingService = hpaPathFindingService;
    }

    public void init(Location lower, Location upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public void loadRsMap() {
        log.info("Started loading RsMap this may take a few moments..");
        for (RegionMap regionMap : PathingEnviroment.loadFrom(PathingEnviroment.REGION_FLAGS, "flags", RegionMap[].class).orElse(null)) {
            RsEnvironment.getRsMap().getRegions().put(Integer.valueOf(regionMap.getKey()), regionMap);
        }
        log.info("Finished loading RsMap with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    @Override
    public void run(String... args) throws Exception {
        loadRsMap();
        RsEnvironment.getRsMap().calculateBounds();
        init(
                new Location(RsEnvironment.getRsMap().getLowestX(), RsEnvironment.getRsMap().getLowestY(), 0),
                new Location(RsEnvironment.getRsMap().getHighestX(), RsEnvironment.getRsMap().getHighestY(), 3)
        );

        pathSupplier = new PathFindingSupplier() {
            @Override
            public Optional<List<? extends Edge>> findPath(Location start, Location end, Predicate<Edge> predicate, boolean ignoreStartBlocked) {
                return new AStarImplementation().addDestinationNode(RsEnvironment.getRsMap().getNode(end)).addStartingNode(RsEnvironment.getRsMap().getNode(start)).findPath(new LocateableHeuristic());
            }

            @Override
            public boolean isDirectlyConnected(Location start, Location end) {
                TileNode sNode = RsEnvironment.getRsMap().getNode(start);
                TileNode endNode = RsEnvironment.getRsMap().getNode(end);
                return sNode.getOutgoingEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
            }

            @Override
            public boolean isBlocked(Location location) {
                return RsEnvironment.getRsMap().getFlagAt(location).map(MapFlags::isBlocked).orElse(false);
            }
        };


        List<WayPoint> wpSet = new ArrayList<>();
        int step = 15;
        int lastValid = step;
        for (int plane = lower.getPlane(); plane < upper.getPlane(); plane++) {
            for (int x = lower.getX(); x < upper.getX(); x += step) {
                for (int y = lower.getY(); y < upper.getY(); y++) {
                    if (lastValid++ < step || pathSupplier.isBlocked(new Location(x, y, plane))) continue;
                    lastValid = 0;
                    wpSet.add(new WayPoint(x, y, plane));
                }
            }
        }

        System.out.println("Inserting: " + wpSet.size());
        wpRepository.insert(wpSet);
        System.out.println("Inserted");






       /* init(null, null);

        HPAGraph hpaGraph = hpaPathFindingService.loadHpa(1);

        StringJoiner wpResults = new StringJoiner(",");
        StringJoiner connectionResults = new StringJoiner(",");


        int wpCount = 0;
        int connectionCount = 0;
        for (HPARegion value : hpaGraph.getRegions().values()) {
            for (HPANode node : value.getNodes().values()) {
                WayPoint wayPoint = new WayPoint(node.getLocation().getX(), node.getLocation().getY(), node.getLocation().getPlane());
                wayPoint.setLatitude(GeoUtil.rsToGeo(wayPoint.getX()));
                wayPoint.setLongitude(GeoUtil.rsToGeo(wayPoint.getY()));
                wayPoint.set_key(node.getLocation().getX() + "_" + node.getLocation().getY() + "_" + node.getLocation().getPlane());
                wpResults.add(GsonUtil.getGson().toJson(wayPoint));
                wpCount++;

                for (Edge edge : node.getOutgoingEdges()) {
                    HPAEdge hpaEdge = (HPAEdge) edge;

                    WayPointConnection connection = new WayPointConnection();
                    connection.set_to("WayPoint/" + hpaEdge.getEnd().getLocation().getX() + "_"  + hpaEdge.getEnd().getLocation().getY() + "_"  + hpaEdge.getEnd().getLocation().getPlane());
                    connection.set_from("WayPoint/" + hpaEdge.getStart().getLocation().getX() + "_"  + hpaEdge.getStart().getLocation().getY() + "_"  + hpaEdge.getStart().getLocation().getPlane());
                    connectionResults.add(GsonUtil.getGson().toJson(connection));
                    connectionCount++;
                }
            }
        }

        wpRepository.getArangoDbService().getDb("Pathing-1").collection("WayPoint").importDocuments("[" + wpResults.toString() + "]");
        wpRepository.getArangoDbService().getDb("Pathing-1").collection("WayPointConnection").importDocuments("[" + connectionResults.toString() + "]");

        System.out.println("Inserted " + wpCount + " wps and " + connectionCount + " connections.");*/
    }
}
