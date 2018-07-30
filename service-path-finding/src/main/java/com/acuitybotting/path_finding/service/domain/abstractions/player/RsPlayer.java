package com.acuitybotting.path_finding.service.domain.abstractions.player;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Zachary Herridge on 7/17/2018.
 */
@Getter
@Setter
@ToString
public class RsPlayer {

    private String ign;
    private String email;

    private int combatLevel;
    private int spellBook;
    private int world;
    private Location location;

    private Map<String, Integer> levels;
    private Map<Integer, Integer> inventory;
    private Map<Integer, Integer> equipment;
    private Map<Integer, Integer> bank;
    private Map<Integer, Integer> questProgress;
}
