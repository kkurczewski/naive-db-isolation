package pl.kkurczewski.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Effectively this class has similar functionality to LinkedList,
 * however this class won't throw ConcurrentModificationException
 **/
public class WeakLinkedList<T> implements Iterable<T> {

    private final T value;
    private WeakLinkedList<T> next;

    public WeakLinkedList(T value, WeakLinkedList<T> next) {
        this.value = value;
        this.next = next;
    }

    public WeakLinkedList(T value) {
        this(value, null);
    }

    public WeakLinkedList(WeakLinkedList<T> next) {
        this(null, next);
    }

    public WeakLinkedList() {
        this(null, null);
    }

    @SafeVarargs
    public static <T> WeakLinkedList<T> of(T... values) {
        var tail = new WeakLinkedList<>(values[values.length - 1]);
        for (int i = values.length - 2; i >= 0; i--) {
            tail = new WeakLinkedList<>(values[i], tail);
        }
        return new WeakLinkedList<>(tail);
    }

    public void append(T value) {
        var current = this;
        while (current.next != null) {
            current = current.next;
        }
        current.next = new WeakLinkedList<>(value);
    }

    public void insert(int index, T value) {
        insertNode(index, new WeakLinkedList<>(value));
    }

    public void insertNode(int index, WeakLinkedList<T> node) {
        if (index < 0 || index > size()) throw new IndexOutOfBoundsException(index);

        var current = this;
        while (--index > -1) current = current.next;

        node.next = current.next;
        current.next = node;
    }

    public WeakLinkedList<T> removeNode(int index) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException(index);

        var current = this;
        while (--index > -1) current = current.next;

        var value = current.next;
        current.next = current.next.next;
        return value;
    }

    public int size() {
        Iterator<T> it = this.iterator();
        int size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        final var current = this;
        return new Iterator<>() {

            private WeakLinkedList<T> next = current;

            @Override
            public boolean hasNext() {
                return next != null && next.next != null;
            }

            @Override
            public T next() {
                next = next.next;
                return next.value;
            }
        };
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        iterator().forEachRemaining(list::add);
        return list;
    }
}
