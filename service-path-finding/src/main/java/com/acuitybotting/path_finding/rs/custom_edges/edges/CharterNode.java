package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

public class CharterNode {

    private static final int COINS = 995;

    private static final String PORT_TYRAS = "Port Tyras";
    private static final String PORT_PHASMATYS = "Port Phasmatys";
    private static final String CATHERBY = "Catherby";
    private static final String SHIPYARD = "Shipyard";
    private static final String MUSA_POINT = "Musa Point";
    private static final String BRIMHAVEN = "Brimhaven";
    private static final String PORT_KHAZARD = "Port Khazard";
    private static final String PORT_SARIM = "Port Sarim";
    private static final String HARMLESS = "Mos Le'Harmless";
    private static final String CORSAIR_COVE = "Corsair Cove";

    private static final String DESTINATION = "DESTINATION";

    private static Collection<CustomEdgeData> connections = new HashSet<>(60);
    private static Collection<CharterNode> charters = new HashSet<>(8);

    static {
        charters.add(new CharterNode()
                .withLocation(new Location(2142, 3122, 0))
                .withShip(new Location(2142, 3125, 1))
                .withName(PORT_TYRAS));

        charters.add(new CharterNode()
                .withLocation(new Location(3702, 3503, 0))
                .withShip(new Location(3705, 3503, 1))
                .withName(PORT_PHASMATYS));

        charters.add(new CharterNode()
                .withLocation(new Location(2792, 3414, 0))
                .withShip(new Location(2792, 3417, 1))
                .withName(CATHERBY));

        charters.add(new CharterNode()
                .withLocation(new Location(3001, 3032, 0))
                .withShip(new Location(2998, 3032, 1))
                .withName(SHIPYARD));

        charters.add(new CharterNode()
                .withLocation(new Location(2954, 3155, 0))
                .withShip(new Location(2957, 3104, 1))
                .withName(MUSA_POINT));

        charters.add(new CharterNode()
                .withLocation(new Location(2760, 3239, 0))
                .withShip(new Location(2763, 3238, 1))
                .withName(BRIMHAVEN));

        charters.add(new CharterNode()
                .withLocation(new Location(2674, 3144, 0))
                .withShip(new Location(2674, 3141, 1))
                .withName(PORT_KHAZARD));

        charters.add(new CharterNode()
                .withLocation(new Location(3038, 3192, 0))
                .withShip(new Location(3038, 3189, 1))
                .withName(PORT_SARIM));

        charters.add(new CharterNode()
                .withLocation(new Location(3671, 2931, 0))
                .withShip(new Location(3668, 2931, 1))
                .withName(HARMLESS));

        charters.add(new CharterNode()
                .withLocation(new Location(2587, 2851, 0))
                .withShip(new Location(2592, 2851, 1))
                .withName(CORSAIR_COVE));

        buildGangplankEdges();
        buildEdges();
    }

    private Location location;
    private Location ship;
    private String name;

    public static void buildGangplankEdges() {
        Interaction cross = new Interaction()
                .setType(Interaction.SCENE_ENTITY)
                .withData("OBJECT_NAME", "Gangplank")
                .withData("OBJECT_ACTION", "Cross")
                .withData("STRICT", false);

        for (CharterNode node : charters) {
            CustomEdgeData inside = new CustomEdgeData()
                    .setStart(node.getShip())
                    .setEnd(node.getLocation())
                    .withInteraction(cross);

            CustomEdgeData outside = new CustomEdgeData()
                    .setStart(node.getLocation())
                    .setEnd(node.getShip())
                    .withInteraction(cross);

            connections.add(inside);
            connections.add(outside);
        }
    }

    public static Collection<CustomEdgeData> getEdges() {
        return connections;
    }

    private static void buildEdges() {
        CharterNode catherby = fromName(CATHERBY);
        assert catherby != null;

        catherby.addEdge(HARMLESS, 1750)
                .addEdge(PORT_PHASMATYS, 1750)
                .addEdge(PORT_SARIM, 500)
                .addEdge(BRIMHAVEN, 240)
                .addEdge(MUSA_POINT, 240)
                .addEdge(SHIPYARD, 800)
                .addEdge(PORT_KHAZARD, 800)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 500);

        CharterNode harmless = fromName(HARMLESS);
        assert harmless != null;

        harmless.addEdge(SHIPYARD, 550)
                .addEdge(PORT_SARIM, 650)
                .addEdge(MUSA_POINT, 2050)
                .addEdge(PORT_KHAZARD, 550)
                .addEdge(CATHERBY, 1250)
                .addEdge(BRIMHAVEN, 1450)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 2040);

        CharterNode sarim = fromName(PORT_SARIM);
        assert sarim != null;

        sarim.addEdge(PORT_PHASMATYS, 650)
                .addEdge(HARMLESS, 650)
                .addEdge(SHIPYARD, 200)
                .addEdge(PORT_KHAZARD, 640)
                .addEdge(BRIMHAVEN, 800)
                .addEdge(CATHERBY, 500)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 600);

        CharterNode phasmatys = fromName(PORT_PHASMATYS);
        assert phasmatys != null;

        phasmatys.addEdge(PORT_SARIM, 650)
                .addEdge(MUSA_POINT, 550)
                .addEdge(SHIPYARD, 1600)
                .addEdge(PORT_KHAZARD, 2050)
                .addEdge(BRIMHAVEN, 1450)
                .addEdge(CATHERBY, 1250)
                .addEdge(CORSAIR_COVE, 2020)
                .addEdge(PORT_TYRAS, 1600);

        CharterNode musa = fromName(MUSA_POINT);
        assert musa != null;

        musa.addEdge(HARMLESS, 550)
                .addEdge(PORT_PHASMATYS, 550)
                .addEdge(CATHERBY, 240)
                .addEdge(BRIMHAVEN, 100)
                .addEdge(PORT_KHAZARD, 200)
                .addEdge(SHIPYARD, 100)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 400);

        CharterNode ship = fromName(SHIPYARD);
        assert ship != null;

        ship.addEdge(HARMLESS, 550)
                .addEdge(PORT_PHASMATYS, 550)
                .addEdge(PORT_SARIM, 200)
                .addEdge(MUSA_POINT, 100)
                .addEdge(PORT_KHAZARD, 360)
                .addEdge(BRIMHAVEN, 200)
                .addEdge(CATHERBY, 800)
                .addEdge(CORSAIR_COVE, 400)
                .addEdge(PORT_TYRAS, 1600);

        CharterNode khazard = fromName(PORT_KHAZARD);
        assert khazard != null;

        khazard.addEdge(PORT_PHASMATYS, 2050)
                .addEdge(HARMLESS, 2050)
                .addEdge(PORT_SARIM, 640)
                .addEdge(MUSA_POINT, 800)
                .addEdge(SHIPYARD, 800)
                .addEdge(BRIMHAVEN, 800)
                .addEdge(CATHERBY, 800)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 300);

        CharterNode brimhaven = fromName(BRIMHAVEN);
        assert brimhaven != null;

        brimhaven.addEdge(HARMLESS, 1950)
                .addEdge(PORT_PHASMATYS, 1450)
                .addEdge(PORT_SARIM, 800)
                .addEdge(MUSA_POINT, 240)
                .addEdge(SHIPYARD, 200)
                .addEdge(PORT_KHAZARD, 200)
                .addEdge(CATHERBY, 240)
                .addEdge(PORT_TYRAS, 1600)
                .addEdge(CORSAIR_COVE, 340);

        CharterNode tyras = fromName(PORT_TYRAS);
        assert tyras != null;

        tyras.addEdge(CORSAIR_COVE, 1600)
                .addEdge(BRIMHAVEN, 1600)
                .addEdge(PORT_KHAZARD, 1600)
                .addEdge(CATHERBY, 1600)
                .addEdge(SHIPYARD, 1600)
                .addEdge(MUSA_POINT, 1600)
                .addEdge(PORT_SARIM, 1600)
                .addEdge(PORT_PHASMATYS, 1600)
                .addEdge(HARMLESS, 1600);

        CharterNode corsair = fromName(CORSAIR_COVE);
        assert corsair != null;

        corsair.addEdge(PORT_TYRAS, 1600)
                .addEdge(PORT_PHASMATYS, 2040)
                .addEdge(BRIMHAVEN, 340)
                .addEdge(CATHERBY, 500)
                .addEdge(PORT_KHAZARD, 300)
                .addEdge(SHIPYARD, 400)
                .addEdge(MUSA_POINT, 400)
                .addEdge(PORT_SARIM, 600)
                .addEdge(HARMLESS, 2040);
    }

    private static CharterNode fromName(String name) {
        for (CharterNode node : charters) {
            if (node.getName().equals(name)) {
                return node;
            }
        }

        return null;
    }

    public CharterNode addEdge(String toString, int cost) {
        CharterNode to = fromName(toString);

        assert to != null;

        CustomEdgeData fromTo = new CustomEdgeData()
                .setStart(getLocation())
                .setEnd(to.getShip())
                .withInteraction(new Interaction().setType(Interaction.CHARTER)
                        .withData(DESTINATION, toString))
                .withRequirement(player -> player.getItems().getCount(COINS) >= cost);

        connections.add(fromTo);

        return this;
    }

    public CharterNode withLocation(Location location) {
        this.location = location;
        return this;
    }

    public CharterNode withShip(Location ship) {
        this.ship = ship;
        return this;
    }

    public Location getShip() {
        return ship;
    }

    public Location getLocation() {
        return location;
    }

    public CharterNode withName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }
}
