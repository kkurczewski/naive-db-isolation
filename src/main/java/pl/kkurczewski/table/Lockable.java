package pl.kkurczewski.table;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Lockable {

    private final Lock lock = new ReentrantLock();

    public Lock lock() {
        lock.lock();
        return lock;
    }
}
