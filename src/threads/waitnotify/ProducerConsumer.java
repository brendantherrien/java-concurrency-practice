package threads.waitnotify;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {

    private final Queue<WorkItem> workQueue = new LinkedList<>();
    private final int queueSize = 10;
    private final Object lock = new Object();

    public static void main(String[] args) {
        ProducerConsumer main = new ProducerConsumer();
        main.start();
    }

    void start() {
        Thread producer = new Producer();
        Thread consumer = new Consumer();

        producer.start();
        consumer.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producer.interrupt();
        consumer.interrupt();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {

        }

    }

    class WorkItem {
        private final String name;

        WorkItem(String name) {
            this.name = name;
        }

        void process() throws InterruptedException {
            System.out.println("Starting a work item: " + name);
            Thread.sleep(500);
            System.out.println("Work item processed: " + name);
        }
    }

    class Producer extends Thread {
        @Override
        public void run() {
            int name = 0;
            while (true) {
                synchronized (lock) {
                    if (workQueue.size() < queueSize) {
                        WorkItem workItem = new WorkItem(String.valueOf(name));
                        workQueue.add(workItem);
                        lock.notify();
                        System.out.println("populated a work item: " + name);
                        name++;
                    } else {
                        System.out.println("the queue is full...");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        }
    }

    class Consumer extends Thread {
        @Override
        public void run() {
            while (true) {
                WorkItem workItem;
                synchronized (lock) {
                    while (true) {
                        workItem = workQueue.poll();
                        lock.notify();
                        if (workItem == null) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        } else { break; }
                    }
                }
                try {
                    workItem.process();
                } catch (InterruptedException e) {
                    return;
                }
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        }
    }

}
