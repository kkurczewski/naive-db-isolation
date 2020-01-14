package pl.kkurczewski.tx;

import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

public abstract class Transaction<T> implements Closeable {

    protected final List<Lock> locks = new ArrayList<>();
    protected List<Table<T>> tables;

    @SafeVarargs
    Transaction(Table<T>... tables) {
        this.tables = List.of(tables);
    }

    public abstract void execute(Consumer<Record<T>> action);

    public void commit() {
        close();
        tables = null;
    }

    public void rollback() {
        close();
        tables = null;
    }

    @Override
    public void close() {
        for (Lock lock : locks) {
            lock.unlock();
        }
        locks.clear();
    }
}
