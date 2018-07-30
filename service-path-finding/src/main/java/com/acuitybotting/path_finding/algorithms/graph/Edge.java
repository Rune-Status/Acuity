package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Map;

public interface Edge {

    default Edge copyWithStart(Node start){
        throw new UnsupportedOperationException();
    }

    boolean isTwoWayEdge();

    Node getStart();

    Node getEnd();

    default double getCostPenalty(){
        return 0;
    }

    default boolean evaluate(GraphState state, Map<String, Object> args){
        return true;
    }
}
