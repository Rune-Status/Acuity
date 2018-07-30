package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface Node {

    default GraphState effectState(GraphState graphState){
        return graphState;
    }

    default Collection<Edge> getOutgoingEdges() {
        return getOutgoingEdges(null, Collections.emptyMap());
    }

    Collection<Edge> getOutgoingEdges(GraphState state, Map<String, Object> args);

    default Collection<Edge> getIncomingEdges(){
        return Collections.emptySet();
    }
}
