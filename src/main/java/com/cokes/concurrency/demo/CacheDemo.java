package com.cokes.concurrency.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheDemo<K,V> {
    final Map<K,V> m = new HashMap<>();
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    // 读锁
    Lock r = readWriteLock.readLock();
    // 写锁
    Lock w = readWriteLock.writeLock();

    V get (K key) {
        r.lock();
        try{
            return m.get(key);
        }finally {
            r.unlock();
        }
    }

    V put(K key, V value) {
        w.lock();
        try{
            return m.put(key,value);
        }finally {
            w.unlock();
        }
    }
}
