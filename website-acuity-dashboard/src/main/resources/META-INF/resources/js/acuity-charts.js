function createChart(chartDivId, chartConfig){
    var chart = Highcharts.chart(chartDivId, JSON.parse(chartConfig));
    chart.prototype.addOrUpdate = function(seriesIndex, timestamp, value){
        var series = chart.series[seriesIndex];
        var lastPoint = series.data[series.data.length - 1];
        if (lastPoint.x === timestamp) {
            lastPoint.update(value);
        } else {
            series.addPoint([timestamp, value], true, true);
        }
    }
}