package com.acuitybotting.data.flow.messaging.services.db.domain;

import com.google.gson.JsonObject;

import java.util.Map;

public class Document {

    private String key;
    private String revision;

    private String database;
    private String principalId;
    private String subGroup;
    private String subKey;
    private JsonObject subDocument;

    private Map<String, Object> headers;

    public String getKey() {
        return key;
    }

    public String getRevision() {
        return revision;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public String getSubKey() {
        return subKey;
    }

    public JsonObject getDocument() {
        return subDocument;
    }

    public String getDatabase() {
        return database;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Document{");
        sb.append("key='").append(key).append('\'');
        sb.append(", revision='").append(revision).append('\'');
        sb.append(", database='").append(database).append('\'');
        sb.append(", principalId='").append(principalId).append('\'');
        sb.append(", subGroup='").append(subGroup).append('\'');
        sb.append(", subKey='").append(subKey).append('\'');
        sb.append(", subDocument='").append(subDocument).append('\'');
        sb.append(", headers=").append(headers);
        sb.append('}');
        return sb.toString();
    }
}