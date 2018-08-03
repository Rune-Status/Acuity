package com.acuitybotting.acuity.rabbit_db.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Zachary Herridge on 8/3/2018.
 */
@Getter
@Setter
@ToString
@Document("RabbitDocument")
public class StringRabbitDocument {

    private String _key;

    private String _rev;

    private String database;
    private String principalId;

    private String subGroup;
    private String subKey;

    private String subDocument;

    private Map<String, Object> headers;

}
