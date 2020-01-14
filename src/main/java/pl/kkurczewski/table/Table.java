package pl.kkurczewski.table;

import pl.kkurczewski.collection.WeakLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

public class Table<T> extends Lockable {

    private final WeakLinkedList<Record<T>> records;

    private Table(WeakLinkedList<Record<T>> records) {
        this.records = records;
    }

    public static <T> Table<T> of(WeakLinkedList<Record<T>> records) {
        return new Table<>(records);
    }

    public static <T> Table<T> of(List<T> records) {
        WeakLinkedList<Record<T>> weakLinkedList = new WeakLinkedList<>();
        records.stream().map(Record::of).forEach(weakLinkedList::append);
        return Table.of(weakLinkedList);
    }

    @SuppressWarnings("unchecked")
    public static <T> Table<T> of(T... records) {
        return of(List.of(records));
    }

    public WeakLinkedList<Record<T>> getRecords() {
        return records;
    }

    public void mutate(Consumer<WeakLinkedList<Record<T>>> mutation) {
        Lock lock = lock();
        try {
            mutation.accept(records);
        } finally {
            lock.unlock();
        }
    }

    public Table<T> snapshot() {
        List<T> recordsCopy = new ArrayList<>();
        records.iterator().forEachRemaining(record -> recordsCopy.add(record.getValue()));
        return Table.of(recordsCopy);
    }
}
