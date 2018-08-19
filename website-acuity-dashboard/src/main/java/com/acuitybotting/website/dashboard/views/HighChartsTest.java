package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.HighChartsComponent;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "highcharts", layout = RootLayout.class)
public class HighChartsTest extends VerticalLayout {

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        HighChartConfiguration highChartConfiguration = new HighChartConfiguration();
        highChartConfiguration.getChart().setType("bar");

        highChartConfiguration.getTitle().setText("Test title");
        highChartConfiguration.getSubtitle().setText("Test sub title");

        Series series = new Series();
        series.setName("Jane");
        series.getData().add(1);
        series.getData().add(0);
        series.getData().add(4);
        highChartConfiguration.getSeries().add(series);

        add(new HighChartsComponent("chart-1", highChartConfiguration));
    }
}
