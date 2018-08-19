package com.acuitybotting.website.dashboard.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@JavaScript("https://code.highcharts.com/highcharts.src.js")
@JavaScript("https://code.jquery.com/jquery-3.3.1.min.js")
@Route(value = "highcharts", layout = RootLayout.class)
public class HighChartsTest extends Div {

    public HighChartsTest() {
        setId("test-chart");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getUI().ifPresent(ui -> {

            ui.getPage().executeJavaScript("Highcharts.chart('test-chart', {\n" +
                    "        chart: {\n" +
                    "            type: 'bar'\n" +
                    "        },\n" +
                    "        title: {\n" +
                    "            text: 'Fruit Consumption'\n" +
                    "        },\n" +
                    "        xAxis: {\n" +
                    "            categories: ['Apples', 'Bananas', 'Oranges']\n" +
                    "        },\n" +
                    "        yAxis: {\n" +
                    "            title: {\n" +
                    "                text: 'Fruit eaten'\n" +
                    "            }\n" +
                    "        },\n" +
                    "        series: [{\n" +
                    "            name: 'Jane',\n" +
                    "            data: [1, 0, 4]\n" +
                    "        }, {\n" +
                    "            name: 'John',\n" +
                    "            data: [5, 7, 3]\n" +
                    "        }]\n" +
                    "    });");

        });
    }
}
