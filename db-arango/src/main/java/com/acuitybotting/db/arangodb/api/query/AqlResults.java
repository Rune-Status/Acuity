package com.acuitybotting.db.arangodb.api.query;

import com.arangodb.ArangoCursor;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AqlResults<T> implements Iterator<T>, Iterable<T> {

    private final Gson gson = new Gson();

    private Class<T> type;
    private ArangoCursor<String> cursor;

    public AqlResults(Class<T> type, ArangoCursor<String> cursor) {
        this.type = type;
        this.cursor = cursor;
    }

    public Optional<T> getFirst(){
        return hasNext() ? Optional.ofNullable(next()) : Optional.empty();
    }

    public Stream<T> stream(){
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T next() {
        if (!cursor.hasNext()) return null;
        if (type.equals(String.class)) return (T) cursor.next();
        return gson.fromJson(cursor.next(), type);
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
