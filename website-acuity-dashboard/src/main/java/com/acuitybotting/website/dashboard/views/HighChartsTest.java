package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.charts.LineChart;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "rspeer/connections", layout = RootLayout.class)
public class HighChartsTest extends VerticalLayout {

    private final InfluxDbService influxDbService;

    private static final String QUERY = "SELECT last(\"count\") AS \"Last Count\" FROM \"acuitybotting-prod-1\".\"autogen\".\"connections-count\" WHERE time > now() - 3h GROUP BY time(5m) FILL(null)";

    public HighChartsTest(InfluxDbService influxDbService) {
        this.influxDbService = influxDbService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        LineChart chart = new LineChart("connections-chart");
        chart.addSeries("connection", () -> influxDbService.query("acuitybotting-prod-1", QUERY).getFirstValues());
        add(chart);
    }
}
