package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerSettings;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TeleportNode {

    private static final int WATER_RUNE = 555;
    private static final int AIR_RUNE = 556;
    private static final int FIRE_RUNE = 554;
    private static final int EARTH_RUNE = 557;
    private static final int LAW_RUNE = 563;
    private static final int BLOOD_RUNE = 565;
    private static final int SOUL_RUNE = 566;
    private static final int BANANA = 1963;

    private static final String SPELL_NAME = "SPELL_NAME";

    private static Collection<CustomEdgeData> connections = new HashSet<>();
    private static Collection<TeleportNode> teleports = new HashSet<>();

    static {
        /*
         * MODERN SPELLBOOK
         */
        teleports.add(new TeleportNode()
                .withName("HOME_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withLocation(new Location(3222, 3219, 0)));

        teleports.add(new TeleportNode()
                .withName("VARROCK_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(FIRE_RUNE, 1)
                .withRune(LAW_RUNE, 1)
                .withRune(AIR_RUNE, 3)
                .withTablet(8007)
                .withLocation(new Location(3212, 3248, 0)));

        teleports.add(new TeleportNode()
                .withName("FALADOR_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(WATER_RUNE, 1)
                .withRune(LAW_RUNE, 1)
                .withRune(AIR_RUNE, 3)
                .withTablet(8009)
                .withLocation(new Location(2964, 3377, 0)));

        teleports.add(new TeleportNode()
                .withName("LUMBRIDGE_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(EARTH_RUNE, 1)
                .withRune(LAW_RUNE, 1)
                .withRune(AIR_RUNE, 3)
                .withTablet(8008)
                .withLocation(new Location(3221, 3217, 0)));

        teleports.add(new TeleportNode()
                .withName("CAMELOT_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withRune(AIR_RUNE, 5)
                .withRune(LAW_RUNE, 1)
                .withTablet(8010)
                .withLocation(new Location(2756, 3476, 0)));

        teleports.add(new TeleportNode()
                .withName("ARDOUGNE_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(WATER_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withTablet(8011)
                .withLocation(new Location(2662, 3301, 0)));

        teleports.add(new TeleportNode()
                .withName("WATCHTOWER_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(EARTH_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withTablet(8012)
                .withLocation(new Location(2933, 4713, 0)));

        teleports.add(new TeleportNode()
                .withName("TROLLHEIM_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(FIRE_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withLocation(new Location(2888, 3678, 0)));

        teleports.add(new TeleportNode()
                .withName("APE_ATOLL_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(BANANA, 1)
                .withRune(FIRE_RUNE, 2)
                .withRune(WATER_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withLocation(new Location(2799, 2797, 1)));

        teleports.add(new TeleportNode()
                .withName("TELEPORT_TO_KOUREND")
                .withSpellbook(PlayerSettings.SPELLBOOK_MODERN)
                .withRune(SOUL_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withRune(WATER_RUNE, 4)
                .withRune(FIRE_RUNE, 5)
                .withLocation(new Location(1643, 3673, 0)));

        /*
           ANCIENT SPELLBOOK
         */

        teleports.add(new TeleportNode()
                .withName("ANCIENT_HOME_TELEPORT")
                .withLocation(new Location(3089, 3502, 0))
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT));

        teleports.add(new TeleportNode()
                .withName("PADDEWA_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3099, 9883, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(FIRE_RUNE, 1)
                .withRune(AIR_RUNE, 1)
                .withLevel(54));

        teleports.add(new TeleportNode()
                .withName("SENNTISEN_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3322, 3336, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(SOUL_RUNE, 1)
                .withLevel(60));

        teleports.add(new TeleportNode()
                .withName("KHARYLL_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3493, 3472, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(BLOOD_RUNE, 1)
                .withLevel(66));

        teleports.add(new TeleportNode()
                .withName("LASSAR_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3003, 3471, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(WATER_RUNE, 4)
                .withLevel(72));

        teleports.add(new TeleportNode()
                .withName("DAREYAAK_TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(2970, 3698, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(FIRE_RUNE, 3)
                .withRune(AIR_RUNE, 3)
                .wilderness()
                .withLevel(78));

        teleports.add(new TeleportNode()
                .withName("CARRALLANGER TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3160, 3667, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(SOUL_RUNE, 2)
                .wilderness()
                .withLevel(84));

        teleports.add(new TeleportNode()
                .withName("ANNAKARL TELEPORT")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(3287, 3886, 0))
                .withRune(BLOOD_RUNE, 2)
                .withRune(LAW_RUNE, 2)
                .withLevel(90));

        teleports.add(new TeleportNode()
                .withName("GHORROCK")
                .withSpellbook(PlayerSettings.SPELLBOOK_ANCIENT)
                .withLocation(new Location(2973, 3878, 0))
                .withRune(LAW_RUNE, 2)
                .withRune(WATER_RUNE, 8)
                .withLevel(96));

        buildConnections();
    }

    private final List<Integer> runes = new ArrayList<>();
    private final List<Integer> quantities = new ArrayList<>();
    private int tablet;
    private String name;
    private int spellbook;
    private Location location;
    private boolean wilderness;
    private int level;

    public static Collection<CustomEdgeData> getEdges() {
        return connections;
    }

    private static void buildConnections() {
        for (TeleportNode node : teleports) {
            CustomEdgeData edgeData = new CustomEdgeData()
                    .setEnd(node.location)
                    .withInteraction(new Interaction()
                            .setType(Interaction.SPELL)
                            .withData(SPELL_NAME, node.name))
                    .withRequirement(player -> player.getSettings().getSpellBook() == node.spellbook &&
                            (player.getItems().getCount(node.tablet) >= 0
                                    || hasRequiredRunes(player, node)));

            if (node.wilderness) {
                edgeData = edgeData.withInteraction(new Interaction()
                        .setType(Interaction.INTERFACE)
                        .withData("OPTION", "Yes, teleport me now.")
                        .withData("WILDERNESS", true));
            }

            connections.add(edgeData);
        }
    }

    private static boolean hasRequiredRunes(Player player, TeleportNode node) {
        for (int i = 0; i < node.runes.size(); i++) {
            int rune = node.runes.get(i);
            int amount = node.quantities.get(i);

            if (player.getItems().getCount(rune) < amount) {
                return false;
            }
        }

        return true;
    }

    public TeleportNode withTablet(int tabletID) {
        this.tablet = tabletID;
        return this;
    }

    public TeleportNode withRune(int rune, int amount) {
        this.runes.add(rune);
        this.quantities.add(amount);
        return this;
    }

    public TeleportNode withName(String name) {
        this.name = name;
        return this;
    }

    public TeleportNode withSpellbook(int spellbook) {
        this.spellbook = spellbook;
        return this;
    }

    public TeleportNode withLocation(Location location) {
        this.location = location;
        return this;
    }

    public TeleportNode wilderness() {
        this.wilderness = true;
        return this;
    }

    public TeleportNode withLevel(int level) {
        this.level = level;
        return this;
    }

}
