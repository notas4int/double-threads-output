package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutor {
    private final AtomicInteger tmp = new AtomicInteger(0);
    private boolean run = true;
    private final Runnable firstRunnableTask = () -> {
        while (true) {
            synchronized (this) {
                if (!run) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                print();
                run = false;
                notify();
            }
        }
    };

    private final Runnable secondRunnableTask = () -> {
        while (true) {
            synchronized (this) {
                if (run) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                print();
                run = true;
                notify();
            }
        }
    };

    public void startThreads() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(firstRunnableTask);
        executor.execute(secondRunnableTask);

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdownNow();
    }

    private void print() {
        System.out.println(Thread.currentThread().getName() + " " + tmp.incrementAndGet());
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}