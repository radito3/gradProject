package org.elsys.apm.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadHolder {

    private static volatile ThreadHolder instance;

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private AtomicInteger count = new AtomicInteger();

    private ThreadHolder() {
        Future<Integer> future = executor.submit(() -> count.getAndIncrement());
        System.out.println("future done? " + future.isDone());
    }

    static ThreadHolder getInstance() {
        if (instance == null) {
            synchronized (ThreadHolder.class) {
                if (instance == null) {
                    instance = new ThreadHolder();
                }
            }
        }
        return instance;
    }
}
