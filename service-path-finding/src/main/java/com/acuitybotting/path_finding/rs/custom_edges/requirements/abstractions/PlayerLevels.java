package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface PlayerLevels {

    int getLevel(String skillName);

    default boolean hasLevel(String skillName, int lowerBound){
        return getLevel(skillName) >= lowerBound;
    }
}
