package com.acuitybotting.db.arangodb.api.repository;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.query.AqlQuery;
import com.acuitybotting.db.arangodb.api.query.AqlResults;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
public abstract class ArangoRepository<T> {

    private final Class<T> type;
    private final ArangoDbService arangoDbService;
    private String dbName;

    protected ArangoRepository(Class<T> type, ArangoDbService arangoDbService) {
        this.type = type;
        this.arangoDbService = arangoDbService;
    }

    public Stream<T> findByFields(Object... filters) {
        return execute(Aql.findByFields(filters)).stream();
    }

    public Optional<T> findByKey(String key) {
        return findByFields("_key", key).findAny();
    }

    public AqlResults<T> insert(T value){
        return execute(Aql.insert(value));
    }

    public void update(String key, String update) {
        execute(Aql.update(key, update));
    }

    public AqlResults<T> execute(AqlQuery query) {
        if (getCollectionName() != null) query.withParameter("@collection", getCollectionName());
        return new AqlResults<>(
                type,
                arangoDbService.execute(arangoDbService.getDb(dbName), query)
        );
    }

    public abstract String getCollectionName();
}
