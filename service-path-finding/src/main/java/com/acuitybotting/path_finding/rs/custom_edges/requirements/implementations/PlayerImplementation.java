package com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations;

import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerItems;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerLevels;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerSettings;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;

/**
 * Created by Zachary Herridge on 7/17/2018.
 */
public class PlayerImplementation implements Player {

    private RsPlayer player;

    private PlayerItems items;
    private PlayerLevels levels;
    private PlayerSettings settings;

    public PlayerImplementation(RsPlayer rsPlayer) {
        this.player = rsPlayer;
        this.items = new PlayerItemsImplementation(player);
        this.levels = new PlayerLevelsImplementation(player);
        this.settings = new PlayerSettingsImplementation(player);
    }

    @Override
    public PlayerSettings getSettings() {
        return settings;
    }

    @Override
    public PlayerItems getItems() {
        return items;
    }

    @Override
    public PlayerLevels getLevels() {
        return levels;
    }

    public boolean hasItem(int itemId) {
        return getItems().hasItem(itemId);
    }
}
