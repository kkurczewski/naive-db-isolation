package pl.kkurczewski.table;

public class Record<T> extends Lockable {

    private final T val;

    private Record(T val) {
        this.val = val;
    }

    public static <T> Record<T> of(T val) {
        return new Record<>(val);
    }

    public T getValue() {
        return val;
    }

}
