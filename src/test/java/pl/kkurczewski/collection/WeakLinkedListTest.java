package pl.kkurczewski.collection;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class WeakLinkedListTest {

    @Test
    void should_return_next_values() {
        Iterator<Integer> it = WeakLinkedList.of(1, 2, 3).iterator();

        assertThat(it.next()).isEqualTo(1);
        assertThat(it.next()).isEqualTo(2);
        assertThat(it.next()).isEqualTo(3);
    }

    @Test
    void should_support_foreach_loop() {
        WeakLinkedList<Integer> it = WeakLinkedList.of(1, 2, 3);

        int i = 0;
        for (Integer record : it) {
            assertThat(record).isEqualTo(++i);
        }
    }

    @Test
    void should_allow_reorder_elements_during_traversing() {
        WeakLinkedList<Integer> weakLinkedList = WeakLinkedList.of(1, 2, 3);
        Iterator<Integer> it = weakLinkedList.iterator();

        assertThat(it.next()).isEqualTo(1);

        var node = weakLinkedList.removeNode(2);
        weakLinkedList.insertNode(0, node);

        assertThat(it.next()).isEqualTo(2);
        assertThat(it.hasNext()).isFalse();
    }
}
