package com.acuitybotting.db.influx.domain.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@ToString
public class Series {

    private String name;
    private List<String> columns;
    private JsonArray values;
}
