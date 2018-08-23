package com.acuitybotting.db.influx.domain.query;

import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@ToString
public class QueryResult {

    private List<Result> results;

    public Series getFirstSeries(){
        return getResults().get(0).getSeries().get(0);
    }

    public JsonArray getFirstValues(){
        return getResults().get(0).getSeries().get(0).getValues();
    }
}
