package com.acuitybotting.db.arango.acuity.statistic.event.repository;

import com.acuitybotting.db.arango.acuity.statistic.event.domain.StatisticEvent;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 8/22/2018.
 */
public interface StatisticEventRepository extends ArangoRepository<StatisticEvent> {

    boolean existsByTypeAndKey(String type, String key);

}
