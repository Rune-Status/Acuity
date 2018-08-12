package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

public class StairEdgeData {

    private static Collection<CustomEdgeData> connections = new HashSet<>();

    static {
        add((ObstacleEdgeData) new ObstacleEdgeData()
                .withName("Ladder")
                .withAction("Climb-down")
                .withSelection(ObstacleEdgeData.SelectionMode.NEAREST_TO_PLAYER)
                .setStart(new Location(3104, 3162, 0))
                .setEnd(new Location(3104, 9576, 0))
        );
        add((ObstacleEdgeData) new ObstacleEdgeData()
                .withName("Ladder")
                .withAction("Climb-up")
                .withSelection(ObstacleEdgeData.SelectionMode.NEAREST_TO_PLAYER)
                .setStart(new Location(3104, 9576, 0))
                .setEnd(new Location(3104, 3162, 0))
        );
    }

    public static void add(ObstacleEdgeData data) {
        connections.add(data.build());
    }

    public static Collection<CustomEdgeData> getEdges() {
        return connections;
    }
}
