package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
@Getter
public class AStarImplementation {

    public static boolean debugMode = false;
    protected int maxAttempts = 1000000;

    protected AStarHeuristicSupplier heuristicSupplier;
    protected Predicate<Edge> edgePredicate = null;

    protected Map<AStarStore, Edge> pathCache = new HashMap<>();
    protected Map<AStarStore, Double> costCache = new HashMap<>();
    protected PriorityQueue<AStarStore> open = new PriorityQueue<>();

    protected Set<Edge> globalEdges = new HashSet<>();

    protected Set<Node> startingNodes = new HashSet<>();
    protected Set<Node> destinationNodes = new HashSet<>();

    protected Map<String, Object> args = Collections.emptyMap();

    public Optional<List<? extends Edge>> findPath(AStarHeuristicSupplier heuristicSupplier) {
        Objects.requireNonNull(heuristicSupplier);

        this.heuristicSupplier = heuristicSupplier;

        try {
            return execute();
        } catch (Throwable e) {
            log.error("Error during AStar execute.", e);
        }

        return Optional.empty();
    }

    public AStarImplementation addDestinationNode(Node node){
        destinationNodes.add(node);
        return this;
    }

    public AStarImplementation addStartingNode(Node node){
        startingNodes.add(node);
        return this;
    }

    protected Optional<List<? extends Edge>> execute() {
        for (Node node : startingNodes) {
            AStarStore store = AStarStore.get(node);
            open.add(store);
            costCache.put(store, 0d);
        }

        int attempts = 0;
        while (!open.isEmpty()) {
            attempts++;
            AStarStore current = open.poll();

            if (attempts >= maxAttempts) {
                log.warn("Failed to find path after {} attempts.", attempts);
                break;
            }

            if (destinationNodes.contains(current.getNode())) {
                log.debug("Found path from in {} attempts.", attempts);
                List<Edge> path = collectPath(current.getNode());
                if (!debugMode) clear();
                return Optional.ofNullable(path);
            }

            for (Edge globalEdge : globalEdges) {
                evaluate(current, globalEdge);
            }

            for (Edge edge : current.getNode().getOutgoingEdges(current.getState(), args)) {
                evaluate(current, edge);
            }
        }

        if (!debugMode) clear();
        return Optional.empty();
    }

    protected void evaluate(AStarStore current, Edge edge){
        if (edgePredicate != null && !edgePredicate.test(edge)) return;
        if (!edge.evaluate(current.getState(), args)) return;

        Node next = edge.getEnd();

        double newCost = costCache.getOrDefault(current, 0d) + heuristicSupplier.getHeuristic(startingNodes, current.getNode(), Collections.singleton(next), edge);

        AStarStore nextStore = AStarStore.get(next).setState(next.effectState(current.getState()));
        Double oldCost = costCache.get(nextStore);
        if (oldCost == null || newCost < oldCost) {
            nextStore.setPriority(newCost + heuristicSupplier.getHeuristic(startingNodes, next, destinationNodes, edge));
            costCache.put(nextStore, newCost);
            open.add(nextStore);
            pathCache.put(nextStore, edge.getStart() == null ? edge.copyWithStart(current.getNode()) : edge);
        }
    }

    protected List<Edge> collectPath(Node end) {
        List<Edge> path = new ArrayList<>();
        Edge edge = pathCache.get(AStarStore.get(end));
        while (edge != null) {
            path.add(edge);
            if (startingNodes.contains(edge.getStart())) break;
            edge = pathCache.get(AStarStore.get(edge.getStart()));
        }
        Collections.reverse(path);
        return path;
    }

    protected void clear() {
        open.clear();
        costCache.clear();
        pathCache.clear();
    }

    public AStarImplementation setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }

    public AStarImplementation setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public AStarImplementation setEdgePredicate(Predicate<Edge> edgePredicate) {
        this.edgePredicate = edgePredicate;
        return this;
    }

    public AStarImplementation setArgs(Map<String, Object> args) {
        this.args = args;
        return this;
    }
}
