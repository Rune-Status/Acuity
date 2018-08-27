package com.acuitybotting.data.flow.messaging.services.db.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Getter
@ToString
public class RabbitDbRequest {

    public static final int UPSERT = 0;
    public static final int FIND_BY_KEY = 1;
    public static final int FIND_BY_GROUP = 2;
    public static final int DELETE_BY_KEY = 3;

    private Integer type;

    private String database;
    private String group;
    private String key;
    private String rev;

    private String insertDocument;
    private String updateDocument;

    private Map<String, Boolean> options = new HashMap<>();

    public RabbitDbRequest setType(Integer type) {
        this.type = type;
        return this;
    }

    public RabbitDbRequest setDatabase(String database) {
        this.database = database;
        return this;
    }

    public RabbitDbRequest setGroup(String group) {
        this.group = group;
        return this;
    }

    public RabbitDbRequest setKey(String key) {
        this.key = key;
        return this;
    }

    public RabbitDbRequest setRev(String rev) {
        this.rev = rev;
        return this;
    }

    public RabbitDbRequest setInsertDocument(String insertDocument) {
        this.insertDocument = insertDocument;
        return this;
    }

    public RabbitDbRequest setUpdateDocument(String updateDocument) {
        this.updateDocument = updateDocument;
        return this;
    }
}
