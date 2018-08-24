function createChart(chartDivId, chartConfig) {
    Highcharts.chart(chartDivId, JSON.parse(chartConfig));
    document.getElementById(chartDivId).addOrUpdate = function (seriesIndex, timestamp, value) {
        timestamp = parseInt(timestamp);
        var chart = $(this).highcharts();
        console.log("addOrUpdate: " + seriesIndex + ", " + timestamp + ", " + value);
        console.log(chart);
        var series = chart.series[seriesIndex];
        console.log(series);
        var lastPoint = series.data[series.data.length - 1];
        console.log(lastPoint);
        if (lastPoint.x === timestamp) {
            console.log("update");
            lastPoint.update(value);
        } else {
            console.log("add");
            series.addPoint([timestamp, value], true, true);
        }
    }
}