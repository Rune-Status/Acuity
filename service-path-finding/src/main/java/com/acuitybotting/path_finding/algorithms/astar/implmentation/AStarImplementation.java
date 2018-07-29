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

    private boolean debugMode = false;
    private int maxAttempts = 1000000;

    private AStarHeuristicSupplier heuristicSupplier;
    private Predicate<Edge> edgePredicate = null;

    private Map<AStarStore, Edge> pathCache = new HashMap<>();
    private Map<AStarStore, Double> costCache = new HashMap<>();
    private PriorityQueue<AStarStore> open = new PriorityQueue<>();

    private Set<Edge> globalEdges = new HashSet<>();

    private Set<Node> startingNodes = new HashSet<>();
    private Set<Node> destinationNodes = new HashSet<>();

    private Map<String, Object> args = Collections.emptyMap();

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
        AStarStore store = AStarStore.get(node);
        open.add(store);
        costCache.put(store, 0d);
        return this;
    }

    private Optional<List<? extends Edge>> execute() {
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

            Collection<Edge> neighbors = new HashSet<>(current.getNode().getNeighbors(current.getState(), args));
            neighbors.addAll(globalEdges);

            for (Edge edge : neighbors) {
                if (edgePredicate != null && !edgePredicate.test(edge)) continue;
                if (!edge.evaluate(current.getState(), args)) continue;

                Node next = edge.getEnd();

                double newCost = costCache.getOrDefault(current, 0d) + heuristicSupplier.getHeuristic(startingNodes, current.getNode(), Collections.singleton(next), edge);

                AStarStore nextStore = AStarStore.get(next).setState(next.effectState(current.getState()));
                Double oldCost = costCache.get(nextStore);
                if (oldCost == null || newCost < oldCost) {
                    nextStore.setPriority(newCost + heuristicSupplier.getHeuristic(startingNodes, next, destinationNodes, edge));
                    costCache.put(nextStore, newCost);
                    open.add(nextStore);

                    if (edge.getStart() == null){
                        pathCache.put(nextStore, edge.copyWithStart(current.getNode()));
                    }
                    else {
                        pathCache.put(nextStore, edge);
                    }
                }
            }
        }

        if (!debugMode) clear();
        return Optional.empty();
    }

    private List<Edge> collectPath(Node end) {
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

    private void clear() {
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
