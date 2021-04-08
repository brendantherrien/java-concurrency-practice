package threads.ReentrantLock;

import java.util.concurrent.locks.Lock;

public class ReentrantLock {

    private final Lock lock1;
    private int counter1;

    private final Lock lock2;
    private int counter2;


    public static void main(String[] args) throws InterruptedException {
        ReentrantLock main = new ReentrantLock();
        main.start();
    }

    void start() throws InterruptedException {
        WriterThread writer = new WriterThread();
        ReaderThread reader = new ReaderThread();

        writer.start();
        reader.start();

        Thread.sleep(5000);

        writer.interrupt();
        reader.interrupt();

        writer.join();
        reader.join();


    }

    class ReaderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                lock1.lock();
                try {
                    System.out.println("The counter is: " + counter1);
                } finally {
                    lock2.lock();
                    try {
                        System.out.println("counte1 + counter2 " + counter1 + counter2);
                    } finally {
                        lock1.unlock();
                        try {
                            System.out.println("counter 2 is: " + counter2);
                        } finally {
                            lock2.unlock();
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        }
    }

    class WriterThread extends Thread {
        @Override
        public void run() {
            while (true) {
                lock1.lock();
                try {
                    counter1++;
                } finally {
                    lock1.unlock();
                }

                lock2.lock();
                try {
                    counter2++;
                } finally {
                    lock2.unlock();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        }
    }
}
