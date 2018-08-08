package com.acuitybotting.db.arango.acuity.rabbit_db.domain;

import com.arangodb.entity.BaseDocument;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Rev;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * Created by Zachary Herridge on 8/3/2018.
 */
@Getter
@Setter
@ToString
@Document("RabbitDocument")
public class MapRabbitDocument {

    @Id
    private String _id;

    @Rev
    private String _rev;

    private String database;
    private String principalId;

    private String subGroup;
    private String subKey;

    private Map<String, Object> subDocument;

    private Map<String, Object> headers;

}
