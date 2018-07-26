package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface PlayerItems {

    int getCount(int itemId);

    default boolean hasItem(int itemId){
        return getCount(itemId) > 0;
    }
}
