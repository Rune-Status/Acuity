package com.acuitybotting.db.arango.acuity.statistic.event.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/22/2018.
 */
@Document("StatisticEvent")
@Getter
@Setter
@ToString
public class StatisticEvent {

    private String type;
    private String key;
}
