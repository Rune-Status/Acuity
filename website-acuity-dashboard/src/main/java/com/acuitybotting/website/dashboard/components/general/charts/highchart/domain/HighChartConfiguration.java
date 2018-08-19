package com.acuitybotting.website.dashboard.components.general.charts.highchart.domain;

import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Chart;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Series;
import com.acuitybotting.website.dashboard.components.general.charts.highchart.domain.chart.Title;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class HighChartConfiguration {

    private Chart chart = new Chart();

    private Title title = new Title();
    private Title subtitle = new Title();

    private List<Series> series = new ArrayList<>();
}
