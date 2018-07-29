package com.acuitybotting.path_finding.rs.domain.location;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarHeuristicSupplier;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.utils.EdgeType;

import java.util.Set;

public class LocateableHeuristic implements AStarHeuristicSupplier {

    @Override
    public Double getHeuristic(Set<Node> start, Node current, Set<Node> end, Edge edge) {
        double cost = Double.MAX_VALUE;
        if (edge instanceof CustomEdge){
            cost = ((CustomEdge) edge).getCost();
        }
        else {
            Location currentLocation = ((Locateable) current).getLocation();
            for (Node node : end) {
                cost = Math.min(cost, currentLocation.getTraversalCost(((Locateable) node).getLocation()));
            }
        }

        return cost + edge.getCostPenalty();
    }
}