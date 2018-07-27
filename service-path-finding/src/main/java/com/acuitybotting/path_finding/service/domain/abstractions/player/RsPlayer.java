package com.acuitybotting.path_finding.service.domain.abstractions.player;

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

    private int combatLevel;
    private int spellBook;
    private Map<String, Integer> levels;
    private Map<Integer, Integer> inventory;
    private Map<Integer, Integer> equipment;
}
