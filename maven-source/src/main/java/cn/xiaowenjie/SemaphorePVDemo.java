package cn.xiaowenjie;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 利用semaphore的先V（release）再P（acquire）操作，实现线程的前置依赖关系。
 * semaphore的容量是0
 */
public class SemaphorePVDemo {


    public static void main(String[] args) {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);

        new Thread(()->{
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程1最先工作");
            semaphore1.release();
        }).start();

        new Thread(()->{
            try {
                semaphore1.acquire();
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程2然后工作");
            semaphore2.release();
        }).start();

        new Thread(()->{
            try {
                semaphore2.acquire();
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程3最后工作");
        }).start();

    }

}