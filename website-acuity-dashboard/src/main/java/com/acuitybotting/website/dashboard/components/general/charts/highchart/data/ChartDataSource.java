package com.acuitybotting.website.dashboard.components.general.charts.highchart.data;

import com.acuitybotting.db.influx.domain.query.Series;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
@Getter
@Setter
public class ChartDataSource {

    private Supplier<Series> seriesSupplier;
    private List<ChartDataListener> listeners = new ArrayList<>();

    public Series getOrLoad(){
        return seriesSupplier.get();
    }

    public void update() {
        Series series = seriesSupplier.get();
        if (series == null) return;
        for (ChartDataListener listener : listeners) {
            listener.onUpdate(series);
        }
    }

    public static JsonArray getColumns(Series series, int... columns){
        JsonArray results = new JsonArray();

        for (JsonElement value : series.getValues()) {
            JsonArray asJsonArray = value.getAsJsonArray();
            JsonArray point = new JsonArray();
            for (int column : columns) point.add(asJsonArray.get(column));
            results.add(point);
        }

        return results;
    }
}
