package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.NodeType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TerminatingNode extends HPANode {

    private Set<TerminatingEdge> edges = new HashSet<>();

    public TerminatingNode(HPARegion region, Location location, boolean end) {
        super(region, location, NodeType.TERMINATING);

        HPANode hpaNode = region.getNodes().get(location);
        if (hpaNode != null){
            if (end) edges.add(new TerminatingEdge(hpaNode, this));
            else edges.add(new TerminatingEdge(this, hpaNode));
        }
        else {
            Set<HPAGraph.InternalConnection> internalConnections = region.getHpaGraph().findInternalConnections(region, this, -1);
            for (HPAGraph.InternalConnection internalConnection : internalConnections) {
                if (end) edges.add(new TerminatingEdge(internalConnection.getEnd(), this).setPath(internalConnection.getPath(), true));
                else edges.add(new TerminatingEdge(this, internalConnection.getEnd()).setPath(internalConnection.getPath(), false));
            }
        }
    }

    public Set<TerminatingEdge> getEdges() {
        return edges;
    }

    public TerminatingEdge getEdgeTo(HPANode hpaNode){
        for (TerminatingEdge terminatingEdge : getEdges()) {
            if (terminatingEdge.getEnd().equals(hpaNode)) return terminatingEdge;
            if (terminatingEdge.getStart().equals(hpaNode)) return terminatingEdge;
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
