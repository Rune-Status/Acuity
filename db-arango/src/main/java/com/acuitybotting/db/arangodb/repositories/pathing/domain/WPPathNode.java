package com.acuitybotting.db.arangodb.repositories.pathing.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WPPathNode {

    private WayPoint node;
    private WayPointConnection edge;

}
