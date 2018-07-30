package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("Duplicates")
@Slf4j
@Getter
public class ReverseAStarImplementation extends AStarImplementation{

    @Override
    protected Optional<List<? extends Edge>> execute() {
        for (Node node : destinationNodes) {
            AStarStore store = AStarStore.get(node);
            open.add(store);
            costCache.put(store, 0d);
        }

        int attempts = 0;
        while (!open.isEmpty()) {
            attempts++;
            AStarStore current = open.poll();

            if (attempts >= maxAttempts) {
                break;
            }

            if (startingNodes.contains(current.getNode())) {
                log.info("Found path from in {} attempts.", attempts);
                List<Edge> path = collectPath(current.getNode());
                if (!debugMode) clear();
                return Optional.ofNullable(path);
            }

            for (Edge globalEdge : globalEdges) {
                if (globalEdge.getEnd().equals(current.getNode())){
                    for (Node startNode : startingNodes) {
                        evaluate(current, globalEdge.copyWithStart(startNode));
                    }
                }
            }

            for (Edge edge : current.getNode().getIncomingEdges()) {
                evaluate(current, edge);
            }
        }

        log.warn("Failed to find path after {} attempts.", attempts);
        if (!debugMode) clear();
        return Optional.empty();
    }

    @Override
    protected void evaluate(AStarStore current, Edge edge){
        if (edgePredicate != null && !edgePredicate.test(edge)) return;
        if (!edge.evaluate(current.getState(), args)) return;

        Node next = edge.getStart();
        if (next == null) return;

        double newCost = costCache.getOrDefault(current, 0d) + heuristicSupplier.getHeuristic(destinationNodes, current.getNode(), Collections.singleton(next), edge);

        AStarStore nextStore = AStarStore.get(next).setState(next.effectState(current.getState()));
        Double oldCost = costCache.get(nextStore);
        if (oldCost == null || newCost < oldCost) {
            nextStore.setPriority(newCost + heuristicSupplier.getHeuristic(destinationNodes, next, startingNodes, edge));
            costCache.put(nextStore, newCost);
            open.add(nextStore);
            pathCache.put(nextStore, edge);
        }
    }

    @Override
    protected List<Edge> collectPath(Node end) {
        List<Edge> path = new ArrayList<>();
        Edge edge = pathCache.get(AStarStore.get(end));
        while (edge != null) {
            path.add(edge);
            if (destinationNodes.contains(edge.getEnd())) break;
            edge = pathCache.get(AStarStore.get(edge.getEnd()));
        }
        return path;
    }
}
