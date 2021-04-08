package threads.deadlock;

import java.util.HashMap;
import java.util.Map;

public class DeadLock {

    private final Map<Integer, Integer> cacheA = new HashMap<>();
    private final Map<Integer, Integer> cacheB = new HashMap<>();

    private final Object cacheALock = new Object();
    private final Object cacheBLock = new Object();

    public static void main(String[] args) {
        DeadLock main = new DeadLock();
        main.start();
    }

    void start() {
        Thread builder = new Thread(() -> {
            int counter = 0;
            while (true) {
                buildCache(counter, counter, counter);
                counter++;
            }
        });
        Thread searcher = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            int counter = 0;
            while (true) {
                int result = searchInCache(counter);
                System.out.println("Result: " + result);
                counter++;
            }
        });
        builder.start();
        searcher.start();

        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {

        }



    }

    void buildCache(Integer key, Integer valueA, Integer valueB) {
        synchronized (cacheALock) {
            synchronized (cacheBLock) {
                cacheA.put(key, valueA);
                cacheB.put(key, valueB);
            }
        }
    }

    Integer searchInCache(Integer key) {
        synchronized (cacheBLock) {
            synchronized (cacheALock) {
                Integer valueA = cacheA.getOrDefault(key, 0);
                Integer valueB = cacheB.getOrDefault(key, 0);
                return valueA + valueB;
            }
        }
    }

}
