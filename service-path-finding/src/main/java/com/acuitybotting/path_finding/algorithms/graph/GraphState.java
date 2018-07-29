package com.acuitybotting.path_finding.algorithms.graph;

public class GraphState {

    private Object stateI = "default";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphState)) return false;

        GraphState that = (GraphState) o;

        return stateI != null ? stateI.equals(that.stateI) : that.stateI == null;
    }

    @Override
    public int hashCode() {
        return stateI != null ? stateI.hashCode() : 0;
    }
}
