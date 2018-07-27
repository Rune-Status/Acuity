package com.acuitybotting.path_finding.rs.domain.location;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarHeuristicSupplier;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

import java.util.Set;

public class LocateableHeuristic implements AStarHeuristicSupplier {

    @Override
    public Double getHeuristic(Set<Node> start, Node current, Set<Node> end, Edge edge) {
        Location currentLocation = ((Locateable) current).getLocation();

        double minDistance = Double.MAX_VALUE;
        for (Node node : end) {
            minDistance = Math.min(minDistance, currentLocation.getTraversalCost(((Locateable) node).getLocation()));
        }

        return minDistance + edge.getCostPenalty();
    }
}