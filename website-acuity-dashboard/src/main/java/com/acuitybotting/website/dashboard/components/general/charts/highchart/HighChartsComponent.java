package com.acuitybotting.website.dashboard.components.general.charts.highchart;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.HighChartConfiguration;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;

public class HighChartsComponent extends Div {

    private HighChartConfiguration highChartConfiguration;

    public HighChartsComponent(String divId, HighChartConfiguration highChartConfiguration) {
        this.highChartConfiguration = highChartConfiguration;
        setWidth("100%");
        setId(divId);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getUI().ifPresent(ui -> ui.getPage().executeJavaScript("Highcharts.chart($0, JSON.parse($1));", getId().orElseThrow(() -> new RuntimeException("No id set of chart object.")), new Gson().toJson(highChartConfiguration)));
    }
}
