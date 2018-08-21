function addOrUpdateChart(chartId, seriesIndex, timestamp, value) {
    console.log("addOrUpdateChart("  + chartId + ", " + seriesIndex + ", " + timestamp + ", " + value + ");");
    var series = $('#' + chartId).highcharts().series[seriesIndex];
    var lastPoint = series.data[series.data.length - 1];
    if (lastPoint.x === timestamp) {
        lastPoint.update(value);
    } else {
        series.addPoint([timestamp, value], true, true);
    }
}