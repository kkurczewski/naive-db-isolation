package pl.kkurczewski.tx;

import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

public class ReadCommitted<T> extends Transaction<T> {

    @SafeVarargs
    private ReadCommitted(Table<T>... tables) {
        super(tables);
    }

    @SafeVarargs
    public static <T> Transaction<T> begin(Table<T>... tables) {
        return new ReadCommitted<>(tables);
    }

    @Override
    public void execute(Consumer<Record<T>> action) {
        for (Table<T> table : tables) {
            table.getRecords().forEach(record -> {
                Lock lock = record.lock();
                try {
                    action.accept(record);
                } finally {
                    lock.unlock();
                }
            });
        }
    }
}
