package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Map;

public interface Edge {

    Node getStart();

    Node getEnd();

    default double getCostPenalty(){
        return 0;
    }

    default boolean evaluate(Map<String, Object> args){
        return true;
    }
}
