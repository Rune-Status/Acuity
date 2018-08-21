package com.acuitybotting.website.dashboard.components.general.charts.highchart.components;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.InteractiveHighChart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChartContainer extends Div {

    private final InteractiveHighChart interactiveHighChart;

    public ChartContainer(InteractiveHighChart interactiveHighChart) {
        this.interactiveHighChart = interactiveHighChart;
        setId(interactiveHighChart.getChartDivId());
        setWidth("100%");
    }

    public void attachConfiguration() {
        getUI().ifPresent(ui -> ui.access(() -> {
            ui.getPage().executeJavaScript(
                    "Highcharts.chart($0, JSON.parse($1));",
                    interactiveHighChart.getChartDivId(),
                    new Gson().toJson(interactiveHighChart.getHighChartConfiguration())
            );
        }));
    }
}
