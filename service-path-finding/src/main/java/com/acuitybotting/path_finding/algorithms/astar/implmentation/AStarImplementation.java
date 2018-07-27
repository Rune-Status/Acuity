package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Getter
public class AStarImplementation {

    private boolean debugMode = false;
    private int maxAttempts = 1000000;

    private AStarHeuristicSupplier heuristicSupplier;
    private Predicate<Edge> edgePredicate = null;

    private Map<Node, Edge> pathCache = new HashMap<>();
    private Map<Node, Double> costCache = new HashMap<>();
    private PriorityQueue<AStarStore> open = new PriorityQueue<>();

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

        return null;
    }

    public AStarImplementation addDestinationNode(Node node){
        destinationNodes.add(node);
        return this;
    }

    public AStarImplementation addStartingNode(Node node){
        startingNodes.add(node);
        open.add(new AStarStore(node, 0));
        costCache.put(node, 0d);
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

            for (Edge edge : current.getNode().getNeighbors(args)) {
                if (edgePredicate != null && !edgePredicate.test(edge)) continue;
                if (!edge.evaluate(args)) continue;

                Node next = edge.getEnd();

                double newCost = costCache.getOrDefault(current.getNode(), 0d) + heuristicSupplier.getHeuristic(startingNodes, current.getNode(), Collections.singleton(next), edge);
                Double oldCost = costCache.get(next);
                if (oldCost == null || newCost < oldCost) {
                    costCache.put(next, newCost);
                    double priority = newCost + heuristicSupplier.getHeuristic(startingNodes, next, destinationNodes, edge);
                    open.add(new AStarStore(next, priority));
                    pathCache.put(next, edge);
                }
            }
        }

        if (!debugMode) clear();
        return Optional.empty();
    }

    private List<Edge> collectPath(Node end) {
        List<Edge> path = new ArrayList<>();
        Edge edge = pathCache.get(end);
        while (edge != null) {
            path.add(edge);
            if (startingNodes.contains(edge.getStart())) break;
            edge = pathCache.get(edge.getStart());
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
