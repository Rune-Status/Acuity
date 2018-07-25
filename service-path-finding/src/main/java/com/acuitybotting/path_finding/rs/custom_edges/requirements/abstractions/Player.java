package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface Player {

    default PlayerSettings getSettings(){
        return null;
    }

    default PlayerItems getItems(){
        return null;
    }

    default PlayerLevels getLevels(){
        return null;
    }
}
