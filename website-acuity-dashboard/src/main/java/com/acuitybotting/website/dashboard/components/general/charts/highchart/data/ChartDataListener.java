package com.acuitybotting.website.dashboard.components.general.charts.highchart.data;

import com.acuitybotting.db.influx.domain.query.Series;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
public interface ChartDataListener {

    void onUpdate(Series series);
}
