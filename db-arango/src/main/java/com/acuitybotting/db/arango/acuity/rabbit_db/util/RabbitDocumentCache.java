package com.acuitybotting.db.arango.acuity.rabbit_db.util;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.MapRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
public class RabbitDocumentCache {

    private RabbitDocumentRepository repository;

    private String principalId;
    private String database;
    private String subGroup;
    private String subKey;

    private MapRabbitDocument cache;

    public RabbitDocumentCache(RabbitDocumentRepository repository, String principalId, String database, String subGroup, String subKey) {
        this.principalId = principalId;
        this.database = database;
        this.subGroup = subGroup;
        this.subKey = subKey;
    }

    public RabbitDocumentCache(RabbitDocumentRepository repository, MapRabbitDocument cache) {
        this.repository = repository;
        this.cache = cache;
        this.principalId = cache.getPrincipalId();
        this.database = cache.getDatabase();
        this.subGroup = cache.getSubGroup();
        this.subKey = cache.getSubKey();
    }

    public String getDatabase() {
        return database;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public String getSubKey() {
        return subKey;
    }

    public MapRabbitDocument getCache() {
        return cache;
    }

    public MapRabbitDocument updateCache() {
        this.cache = repository.findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(principalId, database, subGroup, subKey).orElse(null);
        return this.cache;
    }
}
