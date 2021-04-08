package threads.newthread;

public class Sales {
    private static int salesByDay[] = {0, 1,2,3,4,5,6,7,8,9,10};

    public static void main(String[] args) {
        int startDay = Integer.parseInt(args[0]);
        int endDay = Integer.parseInt(args[1]);

        Sales sales = new Sales();

        Thread thread = new Thread(() -> sales.calculateTotal(startDay, endDay), "calculation thred");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void calculateTotal(int startDay, int endDay) {
        int salesForPeriod = 0;
        for (int i = startDay; i < endDay; i++) {
            salesForPeriod += salesByDay[i];
        }
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        long threadId = currentThread.getId();

        System.out.println("Total sales: " + salesForPeriod + " \n Thread name: " + threadName + " \n Thread ID: " + threadId);
    }
}
