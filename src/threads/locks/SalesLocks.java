package threads.locks;

import java.time.LocalDateTime;

public class SalesLocks {
    private static final int[] salesByDay = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private long totalSales = 0; // a shared variable
    private long maxSoldItems = 0; // shared variable
    private final Object totalSalesLock = new Object();
    private final Object maxSoldItemsLock = new Object();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int startDay = Integer.parseInt(args[0]);
        int endDay = Integer.parseInt(args[1]);
        int daysPerThread = (int)Math.ceil((endDay - startDay) / 2.0);

        SalesLocks sales = new SalesLocks();

        Thread calculationThread1 = new CalculationThread(sales, startDay, daysPerThread + startDay, "calculation-thread-1");
        Thread calculationThread2 = new CalculationThread(sales, daysPerThread + startDay, endDay, "calculation-thread-2");
        Thread backupThread = new Thread(sales::createBackup, "backup-thread");

        calculationThread1.start();
        calculationThread2.start();
        backupThread.start();

        // do some work
        try {
            calculationThread1.join();
            calculationThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        backupThread.interrupt();
        try {
            backupThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Total sales are: " + sales.totalSales + " execution took: " + totalTime + " milliseconds");
        System.out.println("The max of sold items: " + sales.maxSoldItems);
    }

    public long getTotalSales() {
        synchronized (totalSalesLock) {
            return totalSales;
        }
    }

    private void createBackup() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // writing data into the file...
            System.out.println("total sales the backup value is: " + getTotalSales());

            System.out.println(LocalDateTime.now() + " the backup has created");

            if (Thread.currentThread().isInterrupted())
                return;
        }
    }

    public void applyCalculationResult(long partialSales, long maxItems) {

        synchronized (totalSalesLock) {
            totalSales += partialSales;
        }

        synchronized (maxSoldItemsLock) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            maxSoldItems = Math.max(maxSoldItems, maxItems);
        }
    }

    synchronized private void printCurrentDate() {
        System.out.println(LocalDateTime.now());
    }

    private void calculateTotal(int startDay, int endDay) {
        int salesForPeriod = 0;
        int maxItems = 0;
        for (int i = startDay; i < endDay; i++) {
            salesForPeriod += salesByDay[i];

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("The thread was interrupted");
            }
            maxItems = Math.max(maxItems, salesByDay[i]);
        }
        applyCalculationResult(salesForPeriod, maxItems);

        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        long threadId = currentThread.getId();

        System.out.println("Partial sales are: " + salesForPeriod
                + ", start day is " + startDay + ", end day is " + endDay
                + ", thread id is " + threadId
                + ", thread name is " + threadName);
    }


    static class CalculationThread extends Thread {
        final SalesLocks sales;
        final int startDay;
        final int endDay;

        CalculationThread(SalesLocks sales, int startDay, int endDay, String threadName) {
            super(threadName);
            this.sales = sales;
            this.startDay = startDay;
            this.endDay = endDay;
        }

        @Override
        public void run() {
            sales.calculateTotal(startDay, endDay);
        }
    }

}
