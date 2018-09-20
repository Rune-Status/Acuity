package com.acuitybotting.path_finding.algorithms.wp.selector;

import com.acuitybotting.common.utils.GsonUtil;
import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointConnectionRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.WayPointRepository;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPoint;
import com.acuitybotting.db.arangodb.repositories.pathing.domain.WayPointConnection;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.wp.utils.GeoUtil;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Service
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

    @Override
    public void run(String... args) throws Exception {
        init(null, null);

        HPAGraph hpaGraph = hpaPathFindingService.loadHpa(1);




        try (FileWriter wps = new FileWriter("wps.json", true)){
            for (HPARegion value : hpaGraph.getRegions().values()) {
                for (HPANode node : value.getNodes().values()) {
                    WayPoint wayPoint = new WayPoint(node.getLocation().getX(), node.getLocation().getY(), node.getLocation().getPlane());
                    wayPoint.setLatitude(GeoUtil.rsToGeo(wayPoint.getX()));
                    wayPoint.setLongitude(GeoUtil.rsToGeo(wayPoint.getY()));
                    wayPoint.set_key(node.getLocation().getX() + "_"  + node.getLocation().getY() + "_"  + node.getLocation().getPlane());
                    GsonUtil.getGson().toJson(wayPoint, wps);
                    wps.write("\n");
                }
            }
        }
        catch (Throwable e){
            e.printStackTrace();
        }

/*
        for (Map.Entry<HPANode, String> entry : nodeIdMap.entrySet()) {
            for (Edge edge : entry.getKey().getOutgoingEdges()) {
                HPAEdge hpaEdge = (HPAEdge) edge;

                WayPointConnection connection = new WayPointConnection();
                connection.set_to(hpaEdge.getEnd().getLocation().getX() + "_"  + hpaEdge.getEnd().getLocation().getY() + "_"  + hpaEdge.getEnd().getLocation().getPlane());
                connection.set_from(hpaEdge.getStart().getLocation().getX() + "_"  + hpaEdge.getStart().getLocation().getY() + "_"  + hpaEdge.getStart().getLocation().getPlane());

                System.out.println("Added: connection");
            }
        }*/
    }
}
