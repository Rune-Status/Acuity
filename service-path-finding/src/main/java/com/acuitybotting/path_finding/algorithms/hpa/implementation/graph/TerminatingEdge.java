package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsMap;

import java.util.List;
import java.util.UUID;

public class TerminatingEdge extends HPAEdge{

    private String pathKey;
    private List<Location> path;

    public TerminatingEdge(HPANode start, HPANode end) {
        super(start, end);
    }

    public TerminatingEdge setPath(List<TileEdge> path, boolean reverse) {
        pathKey = UUID.randomUUID().toString();
        this.path = RsMap.convertPath(path, reverse);
        return this;
    }

    @Override
    public String getPathKey() {
        return pathKey;
    }

    @Override
    public List<Location> getPath() {
        return path;
    }
}
