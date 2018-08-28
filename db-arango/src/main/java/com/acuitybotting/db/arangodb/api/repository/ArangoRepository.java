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

    private String[] references;

    protected ArangoRepository(Class<T> type, ArangoDbService arangoDbService) {
        this.type = type;
        this.arangoDbService = arangoDbService;
    }

    public Stream<T> findByFields(Object... filters) {
        return execute(Aql.findByFields(filters).withReferences(references)).stream();
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
        return new AqlResults<>(
                type,
                arangoDbService.getDefaultDb().query(
                        query.withParameter("@collection", getCollectionName()).build(),
                        query.getQueryParameters(),
                        String.class
                )
        );
    }

    public abstract String getCollectionName();
}
