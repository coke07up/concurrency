package com.cokes.concurrency.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * 信号量demo
 */
public class SemaphoreDemo {

    private int count;

    final Semaphore semaphore = new Semaphore(1);

    public int getCount() {
        return count;
    }

    /**
     *
     * @throws InterruptedException
     */
    public void addOne() throws InterruptedException {
        semaphore.acquire();
        try{
            count += 1;
        }finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SemaphoreDemo semaphoreDemo = new SemaphoreDemo();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread t1 = new Thread(()->{
            for (int i = 0; i < 1000; i ++) {
                try {
                    semaphoreDemo.addOne();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        });

        Thread t2 = new Thread(()->{
            for (int i = 0; i < 1000; i ++) {
                try {
                    semaphoreDemo.addOne();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        });
        t1.start();
        t2.start();
        countDownLatch.await();
        System.out.println(semaphoreDemo.count);
    }
}
