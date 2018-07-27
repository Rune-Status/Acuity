package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

import java.util.Set;

public interface AStarHeuristicSupplier {

    Double getHeuristic(Set<Node> start, Node current, Set<Node> end, Edge edge);

}
