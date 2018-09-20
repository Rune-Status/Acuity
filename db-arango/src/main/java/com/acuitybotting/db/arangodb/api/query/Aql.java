package com.acuitybotting.db.arangodb.api.query;

import java.util.Arrays;
import java.util.ListIterator;

public class Aql {

    public static AqlQuery findByKey(String key) {
        return query("LET d = DOCUMENT(@@collection, @key)").withParameter("key", key).withReturn("d");
    }

    public static AqlQuery findByFields(Object... filters) {
        AqlQuery query = Aql.query("FOR d IN @@collection").withReturn("d");
        ListIterator<Object> values = Arrays.asList(filters).listIterator();
        while (values.hasNext()) {
            String key = (String) values.next();
            if (!values.hasNext()) throw new RuntimeException("Non-even parameter count.");
            query.withFilter("d." + key + " = @" + key);
            query.withParameter(key, values.next());
        }
        return query;
    }

    public static AqlQuery upsertByKey(String key, Object insert, Object update) {
        return query("UPSERT {_key : @key}")
                .append("INSERT $insert")
                .append("UPDATE $update")
                .append("IN @@collection")
                .withReturn("{current: NEW, previous: OLD}")
                .withParameters(
                        "key", key,
                        "$insert", insert,
                        "$update", update
                );
    }

    public static AqlQuery update(String key, String update) {
        return query("UPDATE DOCUMENT(@@collection, key) WITH $update IN @@collection").withParameters("key", key, "$update", update);
    }

    public static AqlQuery insert(Object document) {
        return query("INSERT $document IN WayPoint").withParameter("$document", document);
    }

    public static AqlQuery query(String query) {
        return query().append(query);
    }

    public static AqlQuery query() {
        return new AqlQuery();
    }
}
