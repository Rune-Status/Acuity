package com.acuitybotting.website.dashboard.components.general.charts.highchart.components;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChartContainer extends Div {

    private HighChartConfiguration highChartConfiguration;

    public ChartContainer(String divId) {
        setWidth("100%");
        setId(divId);
    }

    public void attachConfiguration(HighChartConfiguration chartConfiguration){
        this.highChartConfiguration = chartConfiguration;
        getUI().ifPresent(ui -> ui.getPage().executeJavaScript("Highcharts.chart($0, JSON.parse($1));", getId().orElseThrow(() -> new RuntimeException("No id set of chart object.")), new Gson().toJson(highChartConfiguration)));
    }
}
