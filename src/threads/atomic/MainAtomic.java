package threads.atomic;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MainAtomic {

    private AtomicInteger sharedVariable = new AtomicInteger(0);
    private volatile boolean isStopped = false;
    private volatile Set<Integer> cache = new HashSet<>();

    public static void main(String[] args)  {
        MainAtomic main = new MainAtomic();
        Thread writerThread1 = new Thread(() -> {
            while (!main.isStopped) {
                int newValue = main.sharedVariable.incrementAndGet();
                System.out.println(newValue + " from thread ID: " + Thread.currentThread().getId());

                Set<Integer> tempCache = main.cache;
                System.out.println("The content of the cache is: " + tempCache + "... thread ID: " + Thread.currentThread().getId());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread writerThread2 = new Thread(() -> {
            while (!main.isStopped) {
                int newValue = main.sharedVariable.incrementAndGet();
                System.out.println(newValue + " from thread ID: " + Thread.currentThread().getId());

                Set<Integer> tempCache = main.cache;
                System.out.println("The content of the cache is: " + tempCache + "... thread ID: " + Thread.currentThread().getId());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread readerThread = new Thread(() -> {
            while (!main.isStopped) {
                System.out.println("Shared var is: " + main.sharedVariable);

                Set<Integer> tempCache = main.cache;
                System.out.println("The content of the cache is: " + tempCache + "... thread ID: " + Thread.currentThread().getId());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        writerThread1.start();
        writerThread2.start();
        readerThread.start();

        Set<Integer> newCache = new HashSet<>();
        newCache.add(1);
        newCache.add(2);
        newCache.add(3);
        main.cache = newCache;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<Integer> newCache2 = new HashSet<>();
        newCache2.add(4);
        newCache2.add(5);
        newCache2.add(6);
        main.cache = newCache2;

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        main.isStopped = true;

        try {
            writerThread1.join();
            writerThread2.join();
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
