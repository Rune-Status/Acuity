package com.acuitybotting.website.dashboard.components.general.charts.highchart;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartContainer;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.components.ChartSeries;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.data.ChartDataSource;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 8/20/2018.
 */
@Getter
@Setter
@Slf4j
public class InteractiveHighChart extends VerticalLayout {

    private static ScheduledExecutorService chartUpdateExecutor = ExecutorUtil.newScheduledExecutorPool(3);

    private HighChartConfiguration highChartConfiguration = new HighChartConfiguration();
    private String chartDivId;

    private ChartContainer chartContainer;

    private Set<ChartDataSource> dataSources = new HashSet<>();

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
        scheduleUpdates(5);
    }

    private void scheduleUpdates(int delaySeconds){
        scheduledFuture = chartUpdateExecutor.scheduleAtFixedRate(() -> dataSources.forEach(dataSource -> {
            try {
                dataSource.update();
            }
            catch (Throwable e){
                log.error("Error during series update.", e);
            }
        }), delaySeconds, delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        scheduledFuture.cancel(true);
    }
}
