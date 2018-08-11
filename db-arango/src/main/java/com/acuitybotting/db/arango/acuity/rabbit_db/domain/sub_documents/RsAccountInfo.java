package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.InheritSubId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RsAccountInfo implements InheritSubId{

    private transient String id;

    private int world;

    @Override
    public void setParentSubId(String id) {
        setId(id);
    }
}
