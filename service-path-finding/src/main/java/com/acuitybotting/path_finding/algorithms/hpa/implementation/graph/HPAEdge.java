package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.GraphState;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class HPAEdge implements Edge {

    @Expose
    protected HPANode start, end;

    @Expose
    protected double costPenalty;

    @Expose
    protected int type;

    @Expose
    protected String pathKey;

    @Expose
    protected CustomEdgeData customEdgeData;

    @Override
    public void reverse() {
        HPANode temp = start;
        start = end;
        end = temp;
    }

    public HPAEdge(HPANode start, HPANode end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean evaluate(GraphState state, Map<String, Object> args) {
        if (customEdgeData == null) return true;
        Player player = (Player) args.get("player");
        if (player == null) return true;
        return customEdgeData.getPlayerPredicates().stream().allMatch(playerPredicate -> playerPredicate.test(player));
    }

    @Override
    public double getCostPenalty() {
        return costPenalty;
    }

    public HPAEdge setPathKey(String pathKey) {
        this.pathKey = pathKey;
        return this;
    }

    public HPAEdge setCostPenalty(double costPenalty) {
        this.costPenalty = costPenalty;
        return this;
    }

    public HPAEdge setType(int type) {
        this.type = type;
        return this;
    }

    public HPAEdge setCustomEdgeData(CustomEdgeData customEdgeDataData) {
        this.customEdgeData = customEdgeDataData;
        return this;
    }

    public List<Location> getPath() {
        return RsEnvironment.getRsMap().getPath(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HPAEdge)) return false;

        HPAEdge hpaEdge = (HPAEdge) o;

        if (Double.compare(this.getCostPenalty(), this.getCostPenalty()) != 0) return false;
        if (getType() != hpaEdge.getType()) return false;
        if (getStart() != null ? !getStart().equals(hpaEdge.getStart()) : hpaEdge.getStart() != null) return false;
        if (getEnd() != null ? !getEnd().equals(hpaEdge.getEnd()) : hpaEdge.getEnd() != null) return false;
        return getPathKey() != null ? getPathKey().equals(hpaEdge.getPathKey()) : hpaEdge.getPathKey() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getStart() != null ? getStart().hashCode() : 0;
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        temp = Double.doubleToLongBits(this.getCostPenalty());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getType();
        result = 31 * result + (getPathKey() != null ? getPathKey().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HPAEdge{" +
                "start=" + start +
                ", end=" + end +
                ", type=" + type +
                '}';
    }
}
