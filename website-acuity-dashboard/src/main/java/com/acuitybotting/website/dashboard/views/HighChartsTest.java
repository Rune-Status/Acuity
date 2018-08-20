package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.db.influx.domain.query.QueryResult;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.HighChartsComponent;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "highcharts", layout = RootLayout.class)
public class HighChartsTest extends VerticalLayout {

    private final InfluxDbService influxDbService;

    public HighChartsTest(InfluxDbService influxDbService) {
        this.influxDbService = influxDbService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        QueryResult query = influxDbService.query("acuitybotting-prod-1", "SELECT max(\"count\") AS \"max_count\" FROM \"acuitybotting-prod-1\".\"autogen\".\"connections-count\" WHERE time > now() - 1h GROUP BY time(5m) FILL(null)");

        HighChartConfiguration highChartConfiguration = new HighChartConfiguration();
        highChartConfiguration.getXAxis().setType("datetime");

        highChartConfiguration.getTitle().setText("RSPeer Connections");

        Series series = new Series();
        series.setName("Connections");
        series.setData(query.getResults().get(0).getSeries().get(0).getValues());
        highChartConfiguration.getSeries().add(series);

        add(new HighChartsComponent("chart-1", highChartConfiguration));
    }
}
