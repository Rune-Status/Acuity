package com.acuitybotting.db.arangodb.repositories.pathing.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WayPointConnection {

    private String _from;
    private String _to;

    private Double weight;

}
