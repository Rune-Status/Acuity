package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

public class WildernessEdge {

    private static Collection<CustomEdgeData> customEdgeData = new HashSet<>();

    static {
        // West wildy
        add(new Location(2947, 3520, 0), new Location(2947, 3523, 0));
        add(new Location(2976, 3520, 0), new Location(2976, 3523, 0));
        add(new Location(2998, 3531, 0), new Location(2995, 3531, 0));

        // Edgeville
        add(new Location(3104, 3520, 0), new Location(3104, 3523, 0));
        add(new Location(3086, 3520, 0), new Location(3086, 3523, 0));
        add(new Location(3074, 3520, 0), new Location(3074, 3523, 0));
        add(new Location(3052, 3520, 0), new Location(3052, 3523, 0));

        // Grand Exchange
        add(new Location(3138, 3520, 0), new Location(3138, 3523, 0));
        add(new Location(3158, 3520, 0), new Location(3158, 3523, 0));
        add(new Location(3188, 3520, 0), new Location(3188, 3523, 0));
        add(new Location(3210, 3520, 0), new Location(3210, 3523, 0));
        add(new Location(3239, 3520, 0), new Location(3239, 3523, 0));
        add(new Location(3278, 3520, 0), new Location(3278, 3523, 0));
        add(new Location(3308, 3520, 0), new Location(3308, 3523, 0));
        add(new Location(3327, 3520, 0), new Location(3327, 3523, 0));
    }

    private static void add(Location start, Location end) {
        CustomEdgeData ditch = new ObstacleEdgeData()
                .withAction("Cross")
                .withName("Wilderness Ditch")
                .withSelection(ObstacleEdgeData.SelectionMode.NEAREST_TO_PLAYER)
                .build()
                .withInteraction(new Interaction()
                        .setType(Interaction.INTERFACE)
                        .withData("PATH", "475>11")
                        .withData("ACTION", "Enter Wilderness"))
                .setStart(start)
                .setEnd(end);

        CustomEdgeData out = new ObstacleEdgeData()
                .withAction("Cross")
                .withName("Wilderness Ditch")
                .withSelection(ObstacleEdgeData.SelectionMode.NEAREST_TO_PLAYER)
                .build()
                .setEnd(start)
                .setStart(end);

        customEdgeData.add(ditch);
        customEdgeData.add(out);
    }

    public static Collection<CustomEdgeData> getEdges() {
        return customEdgeData;
    }
}
