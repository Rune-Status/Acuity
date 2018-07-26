package com.acuitybotting.data.flow.messaging.services.db.domain;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public class RabbitDbRequest {

    public static final int SAVE_REPLACE = 0;
    public static final int FIND_BY_KEY = 1;
    public static final int FIND_BY_GROUP = 2;
    public static final int DELETE_BY_KEY = 3;
    public static final int SAVE_UPDATE = 4;

    private Integer type;

    private String database;
    private String group;
    private String key;
    private String rev;

    private String insertDocument;
    private String updateDocument;

    public Integer getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public RabbitDbRequest setKey(String key) {
        this.key = key;
        return this;
    }

    public RabbitDbRequest setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RabbitDbRequest setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getRev() {
        return rev;
    }

    public RabbitDbRequest setRev(String rev) {
        this.rev = rev;
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public RabbitDbRequest setDatabase(String database) {
        this.database = database;
        return this;
    }

    public String getInsertDocument() {
        return insertDocument;
    }

    public RabbitDbRequest setInsertDocument(String insertDocument) {
        this.insertDocument = insertDocument;
        return this;
    }

    public String getUpdateDocument() {
        return updateDocument;
    }

    public RabbitDbRequest setUpdateDocument(String updateDocument) {
        this.updateDocument = updateDocument;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RabbitDbRequest{");
        sb.append("type=").append(type);
        sb.append(", database='").append(database).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", rev='").append(rev).append('\'');
        sb.append(", insertDocument='").append(insertDocument).append('\'');
        sb.append(", updateDocument='").append(updateDocument).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
