package pl.kkurczewski.tx;

import org.junit.jupiter.api.Test;
import pl.kkurczewski.query.SumQuery;
import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.time.Duration;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.kkurczewski.query.DelayQuery.delay;

class SerializableTest {

    @Test
    void isolation_is_serializable_results_do_not_overlap() {
        var table = Table.of(1, 2, 3);

        try (var tx = Serializable.begin(table)) {
            SumQuery firstQuery = new SumQuery();

            // interrupting query
            var task = runAsync(() -> table.mutate(records -> records.append(Record.of(4))), delayedExecutor(150, MILLISECONDS));

            tx.execute(firstQuery.andThen(delay(Duration.ofMillis(100))));

            tx.commit();

            task.join();
            assertThat(firstQuery.result()).isEqualTo(6);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(1, 2, 3, 4);
        }
    }
}