package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.InheritSubId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class RsAccountInfo implements InheritSubId{

    private transient String id;

    private int world;
    private Map<String, Long> levels;

    @Override
    public void setParentSubId(String id) {
        setId(id);
    }
}
