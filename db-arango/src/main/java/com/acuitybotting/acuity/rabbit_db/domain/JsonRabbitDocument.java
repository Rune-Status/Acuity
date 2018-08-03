package com.acuitybotting.acuity.rabbit_db.domain;

import com.arangodb.springframework.annotation.Document;
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
@Document("RabbitDocument")
public class JsonRabbitDocument {

    private String _key;

    private String _rev;

    private String database;
    private String principalId;

    private String subGroup;
    private String subKey;

    private JsonElement subDocument;

    private Map<String, Object> headers;
}
