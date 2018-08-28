package com.acuitybotting.db.arangodb.api.query;

import com.google.gson.Gson;
import lombok.Getter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class AqlQuery {

    private StringJoiner queryBuilder = new StringJoiner("\n");

    private Map<String, Object> queryParameters = new HashMap<>();
    private Map<String, Object> jsonReplacements = new HashMap<>();

    private Map<String, Boolean> options = new HashMap<>();

    private List<String> references = new ArrayList<>();

    private String returnVarible;

    public AqlQuery append(String query) {
        queryBuilder.add(query);
        return this;
    }

    public AqlQuery withFilter(String comparison) {
        return withFilter(comparison, true);
    }

    public AqlQuery withFilter(String comparison, boolean condition) {
        if (condition) queryBuilder.add("FILTER " + comparison);
        return this;
    }

    public AqlQuery withParameter(String key, Object value) {
        if (key.startsWith("$")) jsonReplacements.put(key, value);
        else queryParameters.put(key, value);
        return this;
    }

    public AqlQuery withParameters(Object... valuesArray) {
        ListIterator<Object> values = Arrays.asList(valuesArray).listIterator();
        while (values.hasNext()) {
            String key = (String) values.next();
            if (!values.hasNext()) throw new RuntimeException("Non-even parameter count.");
            withParameter(key, values.next());
        }
        return this;
    }

    public AqlQuery withInternalReference(String field) {
        references.add(field + ": DOCUMENT({RETURN_VAR}." + field + ")");
        return this;
    }

    public AqlQuery withReference(String variable, String collection, String key) {
        references.add(variable + ": DOCUMENT('" + collection + "', '" + key + "')");
        return this;
    }

    public AqlQuery withRelativeReference(String variable, String suffix) {
        references.add(variable + ": DOCUMENT(@@collection, CONCAT({RETURN_VAR}._key, '" + suffix + "'))");
        return this;
    }

    public AqlQuery withReturn(String value) {
        returnVarible = value;
        return this;
    }

    public String build() {
        if (returnVarible != null) {
            if (references.size() > 0) {
                String collect = references.stream().collect(Collectors.joining(", ", "{", "}")).replaceAll("\\{RETURN_VAR}", returnVarible);
                queryBuilder.add("RETURN MERGE_RECURSIVE(" + returnVarible + ", " + collect + ")");
            } else {
                queryBuilder.add("RETURN " + returnVarible);
            }
        }

        String query = queryBuilder.toString();

        for (Map.Entry<String, Object> entry : jsonReplacements.entrySet()) {
            String json;
            if (entry.getValue() instanceof String) json = (String) entry.getValue();
            else json = new Gson().toJson(entry.getValue());
            query = query.replaceAll(Pattern.quote(entry.getKey()), json);
        }

        return query;
    }

    public static void main(String[] args) {
        System.out.println(
                Aql.findByKey("user1")
                        .withRelativeReference("bank", "_bank")
                        .withInternalReference("bank")
                        .withReference("bank", "itemtables", "bank")
                        .build()
        );
    }
}
