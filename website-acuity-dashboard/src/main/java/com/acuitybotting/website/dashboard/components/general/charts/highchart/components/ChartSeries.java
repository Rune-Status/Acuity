package com.acuitybotting.website.dashboard.components.general.charts.highchart.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
public class ChartSeries {

    private ChartContainer chartContainer;

    private Supplier<JsonArray> loadSupplier;
    private JsonArray data;


    public void addPoint(JsonElement point){
        chartContainer.getUI().ifPresent(ui -> {
            ui.getPage().executeJavaScript("$(\"#$0\").highcharts().series[$1].addPoint(10);");
        });
    }

    private void setPoint(){

    }

}
