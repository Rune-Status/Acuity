package com.acuitybotting.db.arango.acuity.rabbit_db.repository;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.MapRabbitDocument;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public interface RabbitDocumentRepository extends ArangoRepository<MapRabbitDocument> {

    Optional<MapRabbitDocument> findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);

    Set<MapRabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroup(String principalId, String database, String subGroup);

    Set<MapRabbitDocument> findAllByDatabaseAndSubGroup(String database, String subGroup);

    Set<MapRabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroupAndSubDocumentMatchesRegex(String principalId, String database, String subGroup, String regex);

    void deleteAllByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);
}
