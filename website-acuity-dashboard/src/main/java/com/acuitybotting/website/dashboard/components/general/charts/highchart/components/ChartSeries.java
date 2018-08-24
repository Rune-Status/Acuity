package com.acuitybotting.website.dashboard.components.general.charts.highchart.components;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.data.ChartDataListener;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.data.ChartDataSource;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
public class ChartSeries implements ChartDataListener {

    private InteractiveHighChart chart;
    private int[] columns;

    public ChartSeries(InteractiveHighChart chart, int[] columns) {
        this.chart = chart;
        this.columns = columns;
    }

    public void build(String name, JsonArray data) {
        Series series = new Series();
        series.setName(name);
        series.setData(data);
        chart.getHighChartConfiguration().getSeries().add(series);
    }

    @Override
    public void onUpdate(com.acuitybotting.db.influx.domain.query.Series series) {
        if (!chart.getChartContainer().isAttached()) return;

        int index = chart.getSeries().indexOf(this);
        JsonArray data = ChartDataSource.getColumns(series, this.columns);
        JsonArray point = data.get(data.size() - 1).getAsJsonArray();
        if (point.isJsonNull() || point.get(0).isJsonNull() || point.get(1).isJsonNull()) return;
        addOrUpdatePoint(index, point.get(0).getAsLong(), point.get(1).getAsNumber());
    }

    private void addOrUpdatePoint(Number seriesIndex, Number timestamp, Number value) {
        chart.getUI().ifPresent(ui -> ui.access(() -> {
            chart.getChartContainer().getElement().callFunction("addOrUpdate", seriesIndex.intValue(), String.valueOf(timestamp), value.intValue());
        }));
    }
}
