package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import lombok.Getter;

@Getter
public class ObstacleEdgeData extends CustomEdgeData {

    private static final String OBJECT_NAME = "OBJECT_NAME";
    private static final String OBJECT_ACTION = "OBJECT_ACTION";
    private static final String OBJECT_ID = "OBJECT_ID";
    private static final String SELECTION_MODE = "SELECTION_MODE";

    private String name;
    private String action;
    private int id;
    private SelectionMode mode = SelectionMode.ON_TOP;

    public ObstacleEdgeData withName(String name) {
        this.name = name;
        return this;
    }

    public ObstacleEdgeData withAction(String action) {
        this.action = action;
        return this;
    }

    public ObstacleEdgeData withId(int id) {
        this.id = id;
        return this;
    }

    public ObstacleEdgeData withSelection(SelectionMode mode) {
        this.mode = mode;
        return this;
    }

    public CustomEdgeData build() {
        Interaction result = new Interaction().setType(Interaction.SCENE_ENTITY);
        if (id != 0) {
            result.withData(OBJECT_ID, id);
        }

        if (name != null){
            result.withData(OBJECT_NAME, name);
        }

        result.withData(SELECTION_MODE, mode.getId());
        result.withData(OBJECT_ACTION, action);

        return this.withInteraction(result);
    }

    public enum SelectionMode {
        ON_TOP(0),
        NEAREST_TO_START(1),
        NEAREST_TO_PLAYER(2);

        private final int id;

        SelectionMode(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
