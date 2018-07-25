package com.acuitybotting.db.arango.acuity.bot_control.domain;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Getter
@Setter
@ToString
public class RabbitDocument {

    private String _key;

    private String _rev;

    private String database;
    private String principalId;

    private String subGroup;
    private String subKey;

    private JsonElement subDocument;

    private Map<String, Object> headers;
}
