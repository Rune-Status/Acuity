package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

public class FairyRingEdgeData extends CustomEdgeData {

    private static final String FAIRY_CODE = "FAIRY_CODE";
    private static final Location FAIRY_HUB = new Location(2412, 4434, 0);
    private static final int DRAMEN_STAFF_ID = 772;
    private static final int LUNAR_STAFF_ID = 9084;
    private static Collection<CustomEdgeData> customEdgeData = new HashSet<>();

    static {
        add(new FairyRingEdgeData()
                .withCode("aiq")
                .setEnd(new Location(2996, 3114, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("air")
                .setEnd(new Location(2700, 3247, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ajq")
                .setEnd(new Location(2700, 3247, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ajr")
                .setEnd(new Location(2780, 3613, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ajs")
                .setEnd(new Location(2500, 3896, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("akq")
                .setEnd(new Location(2319, 3619, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("aks")
                .setEnd(new Location(2571, 2956, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("alp")
                .setEnd(new Location(2503, 3636, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("alq")
                .setEnd(new Location(3597, 3495, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("alr")
                .setEnd(new Location(3059, 4875, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("als")
                .setEnd(new Location(2644, 3495, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bip")
                .setEnd(new Location(3410, 3324, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("biq")
                .setEnd(new Location(3251, 3095, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bis")
                .setEnd(new Location(2635, 3266, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bjr")
                .setEnd(new Location(2650, 4730, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bjs")
                .setEnd(new Location(2150, 3070, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bkp")
                .setEnd(new Location(2385, 3035, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bkq")
                .setEnd(new Location(3041, 4532, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bkr")
                .setEnd(new Location(3469, 3431, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("bks")
                .setEnd(new Location(2412, 4434, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("blp")
                .setEnd(new Location(2437, 5126, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("blr")
                .setEnd(new Location(2740, 3351, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("cip")
                .setEnd(new Location(2513, 3884, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ciq")
                .setEnd(new Location(2528, 3127, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("cis")
                .setEnd(new Location(1639, 3868, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("cjr")
                .setEnd(new Location(2705, 3576, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ckp")
                .setEnd(new Location(2075, 4848, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("ckr")
                .setEnd(new Location(2801, 3003, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("cks")
                .setEnd(new Location(3447, 3470, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("clp")
                .setEnd(new Location(3082, 3206, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("clr")
                .setEnd(new Location(2740, 2738, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("cls")
                .setEnd(new Location(2682, 3081, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dip")
                .setEnd(new Location(3037, 4763, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("djr")
                .setEnd(new Location(1455, 3658, 0))
        );

        /* HOUSE
        add(new FairyRingEdgeData()
                .withCode("diq")
                .setEnd(new Location(8035, 9899, 0))
        );*/

        add(new FairyRingEdgeData()
                .withCode("dis")
                .setEnd(new Location(3108, 3149, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("djp")
                .setEnd(new Location(2658, 3230, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("djr")
                .setEnd(new Location(1455, 3658, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dkp")
                .setEnd(new Location(2900, 3111, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dkr")
                .setEnd(new Location(3129, 3496, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dks")
                .setEnd(new Location(2744, 3719, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dlq")
                .setEnd(new Location(3423, 3016, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dlr")
                .setEnd(new Location(2213, 3099, 0))
        );

        add(new FairyRingEdgeData()
                .withCode("dls")
                .setEnd(new Location(3501, 9821, 3))
        );
    }

    private FairyRingEdgeData() {
        this.setStart(FAIRY_HUB);
        //TODO: Actually add in the correct values
        this.withRequirement(player -> player.getItems().hasItem(DRAMEN_STAFF_ID) || player.getItems().hasItem(LUNAR_STAFF_ID));
    }

    private static void add(CustomEdgeData edge) {
        if (edge instanceof FairyRingEdgeData) {
            FairyRingEdgeData fairy = (FairyRingEdgeData) edge;
            customEdgeData.add(fairy);
            customEdgeData.add(fairy.toHub());
        }
    }

    public static Collection<CustomEdgeData> getEdges() {
        return customEdgeData;
    }

    private FairyRingEdgeData withCode(String code) {
        return (FairyRingEdgeData) withInteraction(new Interaction().setType(Interaction.FAIRY_RING).withData(FAIRY_CODE, code));
    }

    private CustomEdgeData toHub() {
        return new FairyRingEdgeData()
                .withInteraction(new Interaction().setType(Interaction.FAIRY_RING))
                .setStart(getEnd())
                .setEnd(getStart());
    }
}
