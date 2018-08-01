package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

public class AgilityObstacleEdgeData extends ObstacleEdgeData {

    private static Collection<CustomEdgeData> connections = new HashSet<>();

    static {
/*        add(new AgilityObstacleEdgeData()
                .withLevel(21)
                .withName("Underwall tunnel")
                .withAction("Climb-into")
                .withSelection(SelectionMode.NEAREST_TO_START)
                .setStart(new Location(3142, 3513, 0))
                .setEnd(new Location(3137, 3516, 0)));*/
    }

    public static void add(CustomEdgeData data) {
        AgilityObstacleEdgeData agil = (AgilityObstacleEdgeData) data;
        connections.add(agil
                .build()
                .withRequirement(e -> e.getLevels().hasLevel("AGILITY", agil.getLevel())));

        connections.add(new AgilityObstacleEdgeData()
                .withLevel(agil.getLevel())
                .withSelection(SelectionMode.NEAREST_TO_START)
                .build()
                .withCost(agil.getCost())
                .withRequirement(e -> e.getLevels().hasLevel("AGILITY", agil.getLevel()))
                .setStart(agil.getEnd())
                .setEnd(agil.getStart()));
    }

    private int level;

    public static Collection<CustomEdgeData> getEdges() {
        return connections;
    }

    public AgilityObstacleEdgeData withLevel(int level) {
        this.level = level;
        return this;
    }

    public int getLevel() {
        return level;
    }

}
