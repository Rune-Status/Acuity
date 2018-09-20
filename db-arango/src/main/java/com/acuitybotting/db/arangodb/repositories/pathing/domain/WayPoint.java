package com.acuitybotting.db.arangodb.repositories.pathing.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WayPoint {

    private String _id;
    private String _key;
    private int x, y, plane;

    private double latitude, longitude;

    public WayPoint(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }
}
