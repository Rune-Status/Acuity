package com.acuitybotting.website.dashboard.components.general.charts.highchart;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartContainer;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.google.gson.JsonArray;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
public class InteractiveHighChart extends VerticalLayout {

    private static ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(3);

    private HighChartConfiguration highChartConfiguration = new HighChartConfiguration();
    private String chartDivId;

    private ChartContainer chartContainer;

    private List<ChartSeries> series = new ArrayList<>();

    private ScheduledFuture<?> scheduledFuture;

    public InteractiveHighChart(String divId) {
        this.chartDivId = divId;
        chartContainer = new ChartContainer(this);
        setPadding(false);
        setWidth("100%");
        add(chartContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        chartContainer.attachConfiguration();
        scheduledExecutorService.scheduleAtFixedRate(() -> series.forEach(ChartSeries::update), 3, 1, TimeUnit.SECONDS);
    }
}
