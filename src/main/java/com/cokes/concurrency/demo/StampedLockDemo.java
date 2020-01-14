package com.cokes.concurrency.demo;

import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {
    private int x,y;
    final StampedLock stampedLock = new StampedLock();

    /**
     * 计算到原点的距离
     * @return
     */
    double distanceFromOrigin(){
        // 乐观读
        long stamp = stampedLock.tryOptimisticRead();
        // 读入局部变量，
        // 读的过程数据可能被修改
        int curX = x, curY = y;

        // 判断执行读操作期间，
        // 是否存在写操作，如果存在，
        // 则sl.validate返回false
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try{
                curX = x;
                curY = y;
            }finally {
                stampedLock.unlock(stamp);
            }
        }
        return Math.sqrt( curX * curX + curY * curY);
    }
}
