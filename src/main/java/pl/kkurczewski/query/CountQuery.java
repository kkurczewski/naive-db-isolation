package pl.kkurczewski.query;

import pl.kkurczewski.table.Record;

public class CountQuery implements Query<Integer> {

    private int count = 0;

    @Override
    public void accept(Record<Integer> record) {
        ++count;
    }

    @Override
    public Integer result() {
        return count;
    }
}
