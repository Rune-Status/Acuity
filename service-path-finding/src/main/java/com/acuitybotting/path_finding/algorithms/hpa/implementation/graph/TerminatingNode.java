package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.NodeType;

import java.util.*;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TerminatingNode extends HPANode {

    private Set<TerminatingEdge> connections = new HashSet<>();

    public TerminatingNode(HPARegion region, Location location) {
        super(region, location, NodeType.CUSTOM);
    }

    public TerminatingNode addStartEdges(){
        return this;
    }

    private void connectTemporarily(HPANode start, HPANode end, List<TileEdge> path, boolean reverse){
        TerminatingEdge edge = new TerminatingEdge(start, end);
        if (path != null) edge.setPath(path, reverse);
        edge.setType(EdgeType.BASIC);
        edge.setCost(path == null ? 1 : path.size());
        start.getTemporaryEdges().add(edge);
        connections.add(edge);
    }

    public void disconnectFromGraph(){
        for (TerminatingEdge connection : connections) {
            connection.getStart().getTemporaryEdges().remove(connection);
        }
        connections.clear();
    }

    public void connectToGraph() {
        HPAGraph graph = getHpaRegion().getHpaGraph();

        HPANode hpaNode = getHpaRegion().getNodes().get(getLocation());
        if (hpaNode != null) {
            connectTemporarily(this, hpaNode, null, false);
            connectTemporarily(hpaNode, this, null, true);
        }
        else {
            for (HPAGraph.InternalConnection internalConnection : graph.findInternalConnections(getHpaRegion(), this, -1)) {
                HPANode end = internalConnection.getEnd();
                connectTemporarily(this, end, internalConnection.getPath(), false);
                connectTemporarily(end, this, internalConnection.getPath(), true);
            }
        }
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
