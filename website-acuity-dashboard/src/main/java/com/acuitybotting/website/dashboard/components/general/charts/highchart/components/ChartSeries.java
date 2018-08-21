package com.acuitybotting.website.dashboard.components.general.charts.highchart.components;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
public class ChartSeries {

    private InteractiveHighChart chart;

    private Supplier<JsonArray> loadSupplier;

    public ChartSeries(InteractiveHighChart chart) {
        this.chart = chart;
    }

    public void build(String name) {
        JsonArray data = loadSupplier.get();
        Series series = new Series();
        series.setName(name);
        series.setData(data);
        chart.getHighChartConfiguration().getSeries().add(series);
    }

    public void update() {
        if (loadSupplier == null) return;
        JsonArray data = loadSupplier.get();
        if (data == null) return;

        String chartId = chart.getChartDivId();
        int index = chart.getSeries().indexOf(this);

        JsonArray point = data.get(data.size() - 1).getAsJsonArray();
        addOrUpdatePoint(chartId, index, point.get(0).getAsLong(), point.get(1).getAsNumber());
    }

    private void addOrUpdatePoint(String chartId, Number seriesIndex, Number timestamp, Number value) {
        String js = "addOrUpdateChart(\"" + chartId + "\", " + seriesIndex + ", " + timestamp + ", " + value + ");";

        chart.getUI().ifPresent(ui -> {
            if (ui.isClosing() || ui.getSession() == null) return;
            ui.access(() -> ui.getPage().executeJavaScript(js));
        });
    }
}
