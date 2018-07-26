package com.acuitybotting.path_finding.service.domain;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RsPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PathRequest {

    private Location start, end;
    private RsPlayer rsPlayer;

}
