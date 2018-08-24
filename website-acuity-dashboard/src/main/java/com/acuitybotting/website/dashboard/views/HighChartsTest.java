package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.charts.LineChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.data.ChartDataSource;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "rspeer/connections", layout = RootLayout.class)
public class HighChartsTest extends VerticalLayout {

    private final InfluxDbService influxDbService;

    public HighChartsTest(InfluxDbService influxDbService) {
        this.influxDbService = influxDbService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        LineChart chart = new LineChart("connections-chart");

        ChartDataSource dataSource = new ChartDataSource();
        dataSource.setSeriesSupplier(() -> {
            return influxDbService.query("client-state", "SELECT max(\"count\") AS \"max_count\", max(\"loggedIn\") AS \"max_loggedIn\" FROM \"client-state\".\"autogen\".\"clients-state\" WHERE time > now() - 20m GROUP BY time(5s) FILL(null)").getFirstSeries();
        });
        chart.addSeries("connected", dataSource, 0, 1);
        chart.addSeries("logged in", dataSource, 0, 2);
        add(chart);
    }
}
