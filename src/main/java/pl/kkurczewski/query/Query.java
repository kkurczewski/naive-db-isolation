package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

import java.util.function.Consumer;

public interface Query<T> extends Consumer<Record<T>> {
    T result();
}
