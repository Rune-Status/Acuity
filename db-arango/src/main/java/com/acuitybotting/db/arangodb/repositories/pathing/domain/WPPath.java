package com.acuitybotting.db.arangodb.repositories.pathing.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class WPPath {

    private WayPoint start, end;
    private List<WPPathNode> path;

}
