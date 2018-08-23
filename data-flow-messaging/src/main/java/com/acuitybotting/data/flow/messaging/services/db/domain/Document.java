package com.acuitybotting.data.flow.messaging.services.db.domain;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class Document {

    private String key;
    private String revision;

    private String database;
    private String principalId;
    private String subGroup;
    private String subKey;
    private JsonObject subDocument;

    private Map<String, Object> meta;
}