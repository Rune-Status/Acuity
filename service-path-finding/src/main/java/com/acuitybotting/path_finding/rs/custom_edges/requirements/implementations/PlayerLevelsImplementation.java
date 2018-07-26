package com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations;

import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerLevels;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class PlayerLevelsImplementation implements PlayerLevels {

    private RsPlayer player;

    public PlayerLevelsImplementation(RsPlayer rsPlayer) {
        this.player = rsPlayer;
    }

    @Override
    public int getLevel(String skillName) {
        return player.getLevels().getOrDefault(skillName, 0);
    }
}
