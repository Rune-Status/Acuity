package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface Player {

    default boolean isModernSpellbook() {
        return true;
    }

    default PlayerInventory getInventory(){
        return null;
    }

    default PlayerLevels getLevels(){
        return null;
    }

    default QuestProgress[] getQuestProgress() {return null;}
}
