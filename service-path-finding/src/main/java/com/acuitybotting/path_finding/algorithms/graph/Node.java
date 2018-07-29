package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface Node {

    default GraphState effectState(GraphState graphState){
        return graphState;
    }

    default Collection<Edge> getNeighbors() {
        return getNeighbors(null, Collections.emptyMap());
    }

    Collection<Edge> getNeighbors(GraphState state, Map<String, Object> args);

}
