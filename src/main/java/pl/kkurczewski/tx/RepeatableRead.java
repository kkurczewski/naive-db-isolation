package pl.kkurczewski.tx;

import pl.kkurczewski.collection.WeakLinkedList;
import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.util.function.Consumer;

public class RepeatableRead<T> extends Transaction<T> {

    @SafeVarargs
    private RepeatableRead(Table<T>... table) {
        super(table);
    }

    @SafeVarargs
    public static <T> Transaction<T> begin(Table<T>... tables) {
        return new RepeatableRead<>(tables);
    }

    @Override
    public void execute(Consumer<Record<T>> action) {
        for (Table<T> table : tables) {
            WeakLinkedList<Record<T>> records = table.getRecords();
            for (Record<T> record : records) {
                locks.add(record.lock());
                action.accept(record);
            }
        }
    }
}
