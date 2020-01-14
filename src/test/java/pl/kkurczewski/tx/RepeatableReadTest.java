package pl.kkurczewski.tx;

import org.junit.jupiter.api.Test;
import pl.kkurczewski.query.CountQuery;
import pl.kkurczewski.query.SkipQuery;
import pl.kkurczewski.query.SumQuery;
import pl.kkurczewski.table.Record;
import pl.kkurczewski.table.Table;

import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kkurczewski.query.DelayQuery.delay;
import static pl.kkurczewski.query.OneShotQuery.runOnce;

class RepeatableReadTest {

    @Test
    void isolation_is_repeatableRead_and_phantomRow_occurred_1() {
        var table = Table.of(1, 2, 3);

        try (var tx = RepeatableRead.begin(table)) {
            SumQuery firstQuery = new SumQuery();
            tx.execute(firstQuery.andThen(delay(Duration.ofMillis(50))));

            // interrupting query
            table.mutate(records -> records.removeNode(2));

            SumQuery secondQuery = new SumQuery();
            tx.execute(secondQuery.andThen(delay(Duration.ofMillis(50))));

            tx.commit();

            assertThat(firstQuery.result()).isEqualTo(6);
            assertThat(secondQuery.result()).isEqualTo(3);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(1, 2);
        }
    }

    @Test
    void isolation_is_repeatableRead_and_phantomRow_occurred_2() {
        var table = Table.of(1, 2, 3);

        try (var tx = RepeatableRead.begin(table)) {
            SumQuery firstQuery = new SumQuery();
            tx.execute(firstQuery.andThen(delay(Duration.ofMillis(50))));

            // interrupting query
            table.mutate(records -> records.insert(0, Record.of(4)));

            SumQuery secondQuery = new SumQuery();
            tx.execute(secondQuery.andThen(delay(Duration.ofMillis(50))));

            tx.commit();

            assertThat(firstQuery.result()).isEqualTo(6);
            assertThat(secondQuery.result()).isEqualTo(10);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(4, 1, 2, 3);
        }
    }

    @Test
    void isolation_is_repeatableRead_and_phantomRow_occurred_3() {
        var table = Table.of(1, 2, 3);

        try (var tx = RepeatableRead.begin(table)) {
            CountQuery singleQuery = new CountQuery();

            Runnable interruptingQuery = () -> table.mutate(records -> records.insert(0, Record.of(4)));

            tx.execute(singleQuery.andThen(runInMiddle(interruptingQuery)));

            tx.commit();

            assertThat(singleQuery.result()).isEqualTo(3);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(4, 1, 2, 3);
        }
    }

    @Test
    void isolation_is_readCommittedSnapshot_and_row_move_occurred_1() {
        var table = Table.of(1, 2, 3);

        try (var tx = RepeatableRead.begin(table)) {
            SumQuery singleQuery = new SumQuery();

            Runnable interruptingQuery = () -> table.mutate(records -> {
                var node = records.removeNode(2);
                records.insertNode(0, node);
            });

            tx.execute(singleQuery.andThen(runInMiddle(interruptingQuery)));

            tx.commit();

            assertThat(singleQuery.result()).isEqualTo(3);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(3, 1, 2);
        }
    }

    @Test
    void isolation_is_readCommitted_and_row_move_occurred_2() {
        var table = Table.of(1, 2, 3);

        try (var tx = RepeatableRead.begin(table)) {
            SumQuery singleQuery = new SumQuery();

            Runnable interruptingQuery = () -> table.mutate(records -> {
                var node = records.removeNode(0);
                records.insertNode(2, node);
            });

            tx.execute(singleQuery.andThen(runInMiddle(interruptingQuery)));

            tx.commit();

            assertThat(singleQuery.result()).isEqualTo(7);
            assertThat(table.getRecords().toList().stream().map(Record::getValue)).containsExactly(2, 3, 1);
        }
    }

    private Consumer<Record<Integer>> runInMiddle(Runnable interruptingQuery) {
        return SkipQuery.<Integer>skip(1).andThen(runOnce(interruptingQuery));
    }
}