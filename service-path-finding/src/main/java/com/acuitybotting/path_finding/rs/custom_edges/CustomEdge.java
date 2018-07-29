package com.acuitybotting.path_finding.rs.custom_edges;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;

public class CustomEdge extends HPAEdge {

    private double cost = 0;

    public CustomEdge(HPANode start, HPANode end) {
        super(start, end);
    }

    public double getCost() {
        return cost;
    }

    public CustomEdge setCost(double cost) {
        this.cost = cost;
        return this;
    }
}
