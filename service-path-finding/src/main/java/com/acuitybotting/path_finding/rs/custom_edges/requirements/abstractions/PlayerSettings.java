package com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions;

public interface PlayerSettings {

    int SPELLBOOK_MODERN = 0;
    int SPELLBOOK_ANCIENT = 1;
    int SPELLBOOK_LUNAR = 2;
    int SPELLBOOK_NECROMANCY = 3;

    int getSpellBook();

    default boolean isModernSpellbook() {
        return getSpellBook() == SPELLBOOK_MODERN;
    }
}
