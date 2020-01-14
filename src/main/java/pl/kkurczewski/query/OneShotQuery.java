package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

import java.util.function.Consumer;

public class OneShotQuery<T> implements Consumer<Record<T>> {

    private Runnable action;

    private OneShotQuery(Runnable action) {
        this.action = action;
    }

    public static <T> Consumer<Record<T>> runOnce(Runnable action) {
        return new OneShotQuery<>(action);
    }

    @Override
    public void accept(Record<T> record) {
        if (action == null) {
            return;
        }
        action.run();
        action = null;
    }
}
