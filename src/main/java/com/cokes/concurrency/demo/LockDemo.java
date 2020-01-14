package com.cokes.concurrency.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    private final Lock rt1 = new ReentrantLock();

    int value = 0;

    public void addOne(){
        // 获取锁
        rt1.lock();
        try{
            value += 1;
        }finally {
            // 保证锁能释放
            rt1.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockDemo lockDemo = new LockDemo();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread t1 = new Thread(()->{
            for (int i = 0; i < 1000; i ++) {
                lockDemo.addOne();
            }
            countDownLatch.countDown();
        });
        Thread t2 = new Thread(()->{
            for (int i = 0; i < 1000; i ++) {
                lockDemo.addOne();
            }
            countDownLatch.countDown();
        });
        t1.start();
        t2.start();
        countDownLatch.await();
        System.out.println(lockDemo.value);

    }
}
