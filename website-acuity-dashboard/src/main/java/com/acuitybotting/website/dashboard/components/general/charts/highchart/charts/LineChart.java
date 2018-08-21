package com.acuitybotting.website.dashboard.components.general.charts.highchart.charts;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
public class LineChart extends InteractiveHighChart {

    public LineChart(String divId) {
        super(divId);
        getHighChartConfiguration().getXAxis().setType("datetime");
    }
}
