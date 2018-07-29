package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.GraphState;
import com.acuitybotting.path_finding.algorithms.graph.Node;

public class AStarStore implements Comparable<AStarStore> {

    private GraphState state;
    private Node node;
    private double priority;

    public static AStarStore get(Node node){
        return new AStarStore(node, 0).setState(new GraphState());
    }

    private AStarStore(Node node, double priority) {
        this.node = node;
        this.priority = priority;
    }

    public Node getNode() {
        return node;
    }

    public double getPriority() {
        return priority;
    }

    public AStarStore setPriority(double priority) {
        this.priority = priority;
        return this;
    }

    public AStarStore setState(GraphState state) {
        this.state = state;
        return this;
    }

    public GraphState getState() {
        return state;
    }

    @Override
    public int compareTo(AStarStore o) {
        return Double.compare(getPriority(), o.getPriority());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AStarStore)) return false;

        AStarStore starStore = (AStarStore) o;

        if (state != null ? !state.equals(starStore.state) : starStore.state != null) return false;
        return getNode() != null ? getNode().equals(starStore.getNode()) : starStore.getNode() == null;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (getNode() != null ? getNode().hashCode() : 0);
        return result;
    }
}