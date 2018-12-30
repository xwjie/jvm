package cn.xiaowenjie;

public class ThreadWaitNotify {

    public final static Object monitor = new Object();

    public static void main(String[] args) {
        System.out.println("vs code");

        Thread t1 = new Thread(() -> {
            synchronized (monitor) {
                System.out.println(System.currentTimeMillis() + " t1 start");
                try {
                    System.out.println(System.currentTimeMillis() + " get monitor, wait");
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(System.currentTimeMillis() + " t1 end.");
        });

        Thread t2 = new Thread(() -> {
            System.out.println(System.currentTimeMillis() + " t2 start");

            synchronized (monitor) {
                System.out.println(System.currentTimeMillis() + " get monitor, notify");
                monitor.notify();
                System.out.println(System.currentTimeMillis() + " get monitor, notify end");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // FIXME : synchronized exit, t1 can run;

            System.out.println(System.currentTimeMillis() + " t2 end.");
        });

        t1.start();
        t2.start();
    }
}