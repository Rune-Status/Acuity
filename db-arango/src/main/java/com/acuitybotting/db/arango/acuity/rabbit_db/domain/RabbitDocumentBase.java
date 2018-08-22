package com.acuitybotting.db.arango.acuity.rabbit_db.domain;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Getter
@Setter
@ToString
public class RabbitDocumentBase {

    protected String _key;

    protected String _rev;

    protected String database;
    protected String principalId;

    protected String subGroup;
    protected String subKey;

    protected Map<String, Object> meta;

}
