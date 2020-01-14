package com.cokes.concurrency.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列
 */
public class BlockedQueueDemo<E> {
    final ReentrantLock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    private int maxSize;
    private int front;
    private int rear;
    private Object[] items;

    public BlockedQueueDemo(int arrMaxSize){
        maxSize = arrMaxSize;
        items = new Object[arrMaxSize];
    }

    /**
     * 判断队列是否满
     * @return
     */
    public boolean isFull(){
        final ReentrantLock lock = this.lock;
        try{
            lock.lock();
            return (rear + 1) % maxSize == front;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 判断队列是否为空
     * @return
     */
    public boolean isEmpty(){
        ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return rear == front;
        }finally {
            lock.unlock();
        }
    }


    public void enQueue(E item) throws InterruptedException {
        ReentrantLock lock  = this.lock;
        lock.lock();
        try{
            while (isFull()){
                notFull.await();
            }
            items[rear] = item;
            rear = (rear + 1) % maxSize;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E deQueue() throws InterruptedException {
        ReentrantLock lock = this.lock;
        lock.lock();
        try{
            while(isEmpty()){
                notEmpty.await();
            }
            E value = (E)items[front];
            front = (front + 1) % maxSize;
            notFull.signal();
            return value;
        }finally {
            lock.unlock();
        }
    }

    public int size(){
        ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return (rear + maxSize - front) % maxSize;
        }finally {
            lock.unlock();
        }
    }

}
