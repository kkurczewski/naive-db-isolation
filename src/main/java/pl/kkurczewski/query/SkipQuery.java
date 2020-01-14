package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

import java.util.function.Consumer;

public class SkipQuery<T> implements Consumer<Record<T>> {

    private int times;

    private SkipQuery(int times) {
        this.times = times;
    }

    public static <T> Consumer<Record<T>> skip(int times) {
        return new SkipQuery<>(times);
    }

    @Override
    public void accept(Record<T> record) {
        --times;
    }

    @Override
    public Consumer<Record<T>> andThen(Consumer<? super Record<T>> after) {
        return (record) -> {
            accept(record);
            if (times == -1) {
                after.accept(record);
            }
        };
    }
}
