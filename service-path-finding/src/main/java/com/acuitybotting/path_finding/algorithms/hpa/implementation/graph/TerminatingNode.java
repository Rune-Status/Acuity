package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.edges.TeleportNode;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.NodeType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TerminatingNode extends HPANode {

    private Set<Edge> edges = new HashSet<>();

    public TerminatingNode(HPARegion region, Location location, Player player, boolean end) {
        super(region, location, NodeType.TERMINATING);

        HPANode hpaNode = region.getNodes().get(location);
        if (hpaNode != null){
            if (end) edges.add(new TerminatingEdge(hpaNode, this));
            else edges.add(new TerminatingEdge(this, hpaNode));
        }
        else {
            Set<HPAGraph.InternalConnection> internalConnections = region.getHpaGraph().findInternalConnections(region, this, -1, true);
            for (HPAGraph.InternalConnection internalConnection : internalConnections) {
                if (end) edges.add(new TerminatingEdge(internalConnection.getEnd(), this).setPath(internalConnection.getPath(), true));
                else edges.add(new TerminatingEdge(this, internalConnection.getEnd()).setPath(internalConnection.getPath(), false));
            }
        }

        if (!end){
            for (CustomEdgeData customEdgeData : TeleportNode.getEdges()) {
                if (player == null || customEdgeData.getPlayerPredicates().stream().allMatch(playerPredicate -> playerPredicate.test(player))){
                    HPARegion regionContaining = region.getHpaGraph().getRegionContaining(customEdgeData.getEnd());
                    if (regionContaining != null){
                        HPANode teleportEnd = regionContaining.getNodes().get(customEdgeData.getEnd());
                        if (teleportEnd != null){
                            edges.add(new HPAEdge(this, teleportEnd).setType(EdgeType.CUSTOM).setCustomEdgeData(customEdgeData).setCostPenalty(customEdgeData.getCostPenalty()));
                        }
                    }
                }

            }
        }
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Edge getEdgeTo(HPANode hpaNode){
        for (Edge edge : getEdges()) {
            if (edge.getEnd().equals(hpaNode)) return edge;
            if (edge.getStart().equals(hpaNode)) return edge;
        }
        return null;
    }

    public TerminatingNode addStartEdges(){
        return this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
