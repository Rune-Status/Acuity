package com.acuitybotting.website.dashboard.components.general.charts.highchart;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartContainer;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.google.gson.JsonArray;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
public class InteractiveHighChart extends VerticalLayout {

    private ChartContainer chartContainer;

    private List<ChartSeries> series = new ArrayList<>();

    public InteractiveHighChart(String divId) {
        chartContainer = new ChartContainer(divId);

        setPadding(false);
        setWidth("100%");

        add(chartContainer);
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        load();
        chartContainer.attachConfiguration(chartContainer.getHighChartConfiguration());
    }
}
