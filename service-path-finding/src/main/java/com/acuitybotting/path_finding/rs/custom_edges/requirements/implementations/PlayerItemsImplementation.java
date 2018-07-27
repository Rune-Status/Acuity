package com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations;

import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerItems;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class PlayerItemsImplementation implements PlayerItems {

    private RsPlayer player;

    public PlayerItemsImplementation(RsPlayer player) {
        this.player = player;
    }

    @Override
    public int getCount(int itemId) {
        return player.getInventory().getOrDefault(itemId, 0) + player.getEquipment().getOrDefault(itemId, 0);
    }
}
