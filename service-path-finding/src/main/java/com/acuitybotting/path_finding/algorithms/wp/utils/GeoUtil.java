package com.acuitybotting.path_finding.algorithms.wp.utils;

public class GeoUtil {

    public static double rsToGeo(int cordinate){
        return ((double) cordinate * 20d) / 20000d;
    }

    public static int geoToRs(double geo){
        return (int) ((geo * 20d) / 20000d);
    }
}
