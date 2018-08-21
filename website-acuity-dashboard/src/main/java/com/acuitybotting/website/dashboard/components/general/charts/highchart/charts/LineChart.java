package com.acuitybotting.website.dashboard.components.general.charts.highchart.charts;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.google.gson.JsonArray;

import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
public class LineChart extends InteractiveHighChart {

    public LineChart(String divId) {
        super(divId);
        getHighChartConfiguration().getXAxis().setType("datetime");
    }

    public ChartSeries addSeries(String title, Supplier<JsonArray> loadSupplier) {
        ChartSeries series = new ChartSeries(this);
        series.setLoadSupplier(loadSupplier);
        series.build(title);
        getSeries().add(series);
        return series;
    }
}
