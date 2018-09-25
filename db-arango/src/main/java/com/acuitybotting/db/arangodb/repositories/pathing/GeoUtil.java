package com.acuitybotting.db.arangodb.repositories.pathing;

public class GeoUtil {

    public static double rsToGeo(int cordinate){
        return ((double) cordinate / 1000);
    }

    public static int geoToRs(double geo){
        return (int) (geo * 1000);
    }
}
