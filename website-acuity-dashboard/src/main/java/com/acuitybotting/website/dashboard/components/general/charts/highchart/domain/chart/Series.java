package com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart;

import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Series {

    private String name;
    private JsonArray data = new JsonArray();
}
