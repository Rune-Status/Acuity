package com.acuitybotting.db.influx.domain.query;

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

}
