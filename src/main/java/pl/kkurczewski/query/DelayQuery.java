package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

import java.time.Duration;
import java.util.function.Consumer;

public class DelayQuery {

    public static <T> Consumer<Record<T>> delay(Duration duration) {
        return (ignore) -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
