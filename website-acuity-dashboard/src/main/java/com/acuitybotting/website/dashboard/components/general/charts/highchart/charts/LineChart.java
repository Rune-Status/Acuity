package com.acuitybotting.website.dashboard.components.general.charts.highchart.charts;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.data.ChartDataSource;
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

    public ChartSeries addSeries(String title, ChartDataSource dataSource, int... columns) {
        getDataSources().add(dataSource);

        ChartSeries series = new ChartSeries(this, columns);

        series.build(title, ChartDataSource.getColumns(dataSource.getOrLoad(), columns));
        getSeries().add(series);

        dataSource.getListeners().add(series);
        return series;
    }
}
