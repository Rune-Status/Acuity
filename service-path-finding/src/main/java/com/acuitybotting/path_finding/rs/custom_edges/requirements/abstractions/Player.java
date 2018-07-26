package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface Player {

    PlayerSettings getSettings();

    PlayerItems getItems();

    PlayerLevels getLevels();
}
