package com.acuitybotting.path_finding.rs.custom_edges;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.*;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Getter
public class CustomEdgeData {

    @Expose
    private Location start, end;

    @Expose
    private double cost = 0;

    @Expose
    private double costPenalty = 0;

    @Expose
    private List<Interaction> interactions = new ArrayList<>();

    private Collection<PlayerPredicate> playerPredicates = new HashSet<>();

    public CustomEdgeData setStart(Location start) {
        this.start = start;
        return this;
    }

    public CustomEdgeData setEnd(Location end) {
        this.end = end;
        return this;
    }

    public CustomEdgeData withRequirement(PlayerPredicate predicate) {
        playerPredicates.add(predicate);
        return this;
    }

    public CustomEdgeData withInteraction(Interaction interaction) {
        interactions.add(interaction);
        return this;
    }

    public CustomEdgeData withCost(double cost){
        this.cost = cost;
        return this;
    }

    public CustomEdge toEdge(HPAGraph hpaGraph){
        HPANode startNode = Optional.ofNullable(hpaGraph.getRegionContaining(start)).map(region -> region.getNodes().get(start)).orElse(null);
        HPANode endNode = Optional.ofNullable(hpaGraph.getRegionContaining(end)).map(region -> region.getNodes().get(end)).orElse(null);
        return (CustomEdge) new CustomEdge(startNode, endNode).setCost(costPenalty).setCostPenalty(costPenalty).setType(EdgeType.CUSTOM).setCustomEdgeData(this);
    }
}
