package com.acuitybotting.db.arango.acuity.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Rev;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

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
