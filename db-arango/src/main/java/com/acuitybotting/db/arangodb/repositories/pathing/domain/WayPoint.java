package com.acuitybotting.db.arangodb.repositories.pathing.domain;

import com.acuitybotting.db.arangodb.repositories.pathing.GeoUtil;
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
        this.latitude = GeoUtil.rsToGeo(x);
        this.longitude = GeoUtil.rsToGeo(y);
        this._key = x + "_" + y + "_" + plane;
    }
}
