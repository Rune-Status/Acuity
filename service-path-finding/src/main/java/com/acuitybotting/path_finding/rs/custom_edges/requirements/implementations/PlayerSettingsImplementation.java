package com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations;

import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerSettings;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class PlayerSettingsImplementation implements PlayerSettings {

    private RsPlayer player;

    public PlayerSettingsImplementation(RsPlayer player) {
        this.player = player;
    }

    @Override
    public int getSpellBook() {
        return player.getSpellBook();
    }
}
