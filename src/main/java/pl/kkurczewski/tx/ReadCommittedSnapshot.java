package pl.kkurczewski.tx;

import pl.kkurczewski.collection.WeakLinkedList;
import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReadCommittedSnapshot<T> extends Transaction<T> {

    @SafeVarargs
    private ReadCommittedSnapshot(Table<T>... tables) {
        super(tables);
    }

    @SafeVarargs
    public static <T> Transaction<T> begin(Table<T>... tables) {
        return new ReadCommittedSnapshot<>(tables);
    }

    @Override
    public void execute(Consumer<Record<T>> action) {
        List<Table<T>> tablesSnapshot = tables.stream().map(Table::snapshot).collect(Collectors.toList());
        for (Table<T> table : tablesSnapshot) {
            WeakLinkedList<Record<T>> records = table.getRecords();
            for (Record<T> record : records) {
                action.accept(record);
            }
        }
    }
}
