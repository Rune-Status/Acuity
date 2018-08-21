package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.charts.LineChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "highcharts", layout = RootLayout.class)
public class HighChartsTest extends VerticalLayout {

    private final InfluxDbService influxDbService;

    private static final String QUERY = "SELECT count(\"count\") AS \"max_count\" FROM \"acuitybotting-prod-1\".\"autogen\".\"connections-count\" WHERE time > now() - 1m GROUP BY time(10s) FILL(null)";

    public HighChartsTest(InfluxDbService influxDbService) {
        this.influxDbService = influxDbService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        LineChart chart = new LineChart("connections-chart");

        ChartSeries series = new ChartSeries(chart);
        series.setLoadSupplier(() -> influxDbService.query("acuitybotting-prod-1", QUERY).getResults().get(0).getSeries().get(0).getValues());
        series.build("connection");
        chart.getSeries().add(series);

        add(chart);
    }
}
