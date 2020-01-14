package pl.kkurczewski.tx;

import pl.kkurczewski.collection.WeakLinkedList;
import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.util.function.Consumer;

public class Serializable<T> extends Transaction<T> {

    @SafeVarargs
    private Serializable(Table<T>... tables) {
        super(tables);
        for (Table<T> table : this.tables) {
            locks.add(table.lock());
        }
    }

    @SafeVarargs
    public static <T> Transaction<T> begin(Table<T>... tables) {
        return new Serializable<>(tables);
    }

    @Override
    public void execute(Consumer<Record<T>> action) {
        for (Table<T> table : tables) {
            WeakLinkedList<Record<T>> records = table.getRecords();
            for (Record<T> record : records) {
                action.accept(record);
            }
        }
    }
}
