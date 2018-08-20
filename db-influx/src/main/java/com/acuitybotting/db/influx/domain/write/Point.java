package com.acuitybotting.db.influx.domain.write;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
public class Point {

    private String measurement;
    private Map<String, String> tags = new HashMap<>();
    private Map<String, Number> fields = new HashMap<>();
    private Long time;

    public String toLineProtocol(){
        StringBuilder line = new StringBuilder();

        line.append(measurement);

        String tagsLine = tags.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));
        if (!tagsLine.isEmpty()) {
            line.append(",").append(tagsLine);
        }

        String fieldsLine = fields.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));
        if (!fieldsLine.isEmpty()) line.append(" ").append(fieldsLine);

        if (time != null) line.append(" ").append(String.valueOf(time));

        return line.toString();
    }
}
