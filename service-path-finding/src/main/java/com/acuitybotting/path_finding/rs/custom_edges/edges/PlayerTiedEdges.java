package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class PlayerTiedEdges {

    private static Collection<CustomEdgeData> customEdgeData = new HashSet<>();

    private static String SPELL_NAME = "SPELL_NAME";

    static {
        customEdgeData.add(
                new CustomEdgeData()
                        .setEnd(new Location(3221, 3218, 0))
                        .withInteraction(new Interaction().setType(Interaction.SPELL).withData(SPELL_NAME, "HOME_TELEPORT"))
                        .withRequirement(Player::isModernSpellbook)
        );

        customEdgeData.add(
                new CustomEdgeData()
                        .setEnd(new Location(3212, 3424, 0))
                        .withInteraction(new Interaction().setType(Interaction.SPELL).withData(SPELL_NAME, "VARROCK_TELEPORT"))
                        .withRequirement(Player::isModernSpellbook)
        );

        customEdgeData.add(
                new CustomEdgeData()
                        .setEnd(new Location(2757, 3480, 0))
                        .withInteraction(new Interaction().setType(Interaction.SPELL).withData(SPELL_NAME, "CAMELOT_TELEPORT"))
                        .withRequirement(Player::isModernSpellbook)
        );
    }

    public static Collection<CustomEdgeData> getEdges() {
        return customEdgeData;
    }
}
