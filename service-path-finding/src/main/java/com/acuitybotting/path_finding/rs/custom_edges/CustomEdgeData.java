package com.acuitybotting.path_finding.rs.custom_edges;

import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Getter
public class CustomEdgeData {

    @Expose
    private Location start, end;

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
        this.costPenalty = cost;
        return this;
    }
}
