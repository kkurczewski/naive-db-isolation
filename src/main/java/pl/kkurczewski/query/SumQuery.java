package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

public class SumQuery implements Query<Integer> {

    private int sum = 0;

    @Override
    public void accept(Record<Integer> record) {
        sum += record.getValue();
    }

    @Override
    public Integer result() {
        return sum;
    }
}
