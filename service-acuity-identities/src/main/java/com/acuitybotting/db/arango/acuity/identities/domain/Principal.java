package com.acuitybotting.db.arango.acuity.identities.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/2/2018.
 */
@Getter
@Setter
@ToString
public class Principal {

    private String type;
    private String uid;

    public static Principal of(String type, String uid){
        Principal principal = new Principal();
        principal.setType(type);
        principal.setUid(uid);
        return principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Principal)) return false;

        Principal principal = (Principal) o;

        if (getType() != null ? !getType().equals(principal.getType()) : principal.getType() != null) return false;
        return getUid() != null ? getUid().equals(principal.getUid()) : principal.getUid() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getUid() != null ? getUid().hashCode() : 0);
        return result;
    }
}
