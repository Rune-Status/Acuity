package com.acuitybotting.db.arango.path_finding.domain.xtea;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Getter
@Setter
@ToString
public class Xtea {

    private long revision;
    private long region;
    private ArrayList<Long> keys;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Xtea)) return false;

        Xtea xtea = (Xtea) o;

        if (getRevision() != xtea.getRevision()) return false;
        if (getRegion() != xtea.getRegion()) return false;
        return getKeys() != null ? getKeys().equals(xtea.getKeys()) : xtea.getKeys() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getRevision() ^ (getRevision() >>> 32));
        result = 31 * result + (int) (getRegion() ^ (getRegion() >>> 32));
        result = 31 * result + (getKeys() != null ? getKeys().hashCode() : 0);
        return result;
    }
}
