package com.acuitybotting.acuity.rabbit_db.repository;

import com.acuitybotting.acuity.rabbit_db.domain.StringRabbitDocument;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public interface RabbitDocumentRepository extends ArangoRepository<StringRabbitDocument> {

    Optional<StringRabbitDocument> findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);

    Set<StringRabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroup(String principalId, String database, String subGroup);

    Set<StringRabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroupAndSubDocumentMatchesRegex(String principalId, String database, String subGroup, String regex);

    void deleteAllByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);
}
