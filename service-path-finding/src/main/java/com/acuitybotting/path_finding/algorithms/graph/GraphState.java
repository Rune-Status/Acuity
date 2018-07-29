package com.acuitybotting.path_finding.algorithms.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GraphState {

    private String stateId = "default";
    private Map<String, Object> data = new HashMap<>();

    public GraphState() {
    }

    public GraphState(String stateId, Map<String, Object> data) {
        this.stateId = stateId;
        this.data = data;
    }

    public GraphState cloneDataToNewId(){
        return new GraphState(UUID.randomUUID().toString(), new HashMap<>(data));
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphState)) return false;

        GraphState that = (GraphState) o;

        return stateId != null ? stateId.equals(that.stateId) : that.stateId == null;
    }

    @Override
    public int hashCode() {
        return stateId != null ? stateId.hashCode() : 0;
    }
}
