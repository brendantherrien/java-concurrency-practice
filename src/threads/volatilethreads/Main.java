package threads.volatilethreads;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private volatile int sharedVariable = 0;
    private volatile boolean isStopped = false;
    private volatile Set<Integer> cache = new HashSet<>();

    public static void main(String[] args)  {
        Main main = new Main();
        Thread writerThread = new Thread(() -> {
            while (!main.isStopped) {
                main.sharedVariable += 1;

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

        writerThread.start();
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
            writerThread.join();
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
