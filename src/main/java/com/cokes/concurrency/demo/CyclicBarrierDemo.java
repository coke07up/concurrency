package com.cokes.concurrency.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CyclicBarrierDemo {
    Vector<Order> pos;
    Vector<Order> dos;
    Executor executor =  Executors.newFixedThreadPool(1);
    final CyclicBarrier barrier = new CyclicBarrier(2,()->{
        executor.execute(()->check());
    });

    private void check() {
        Order p = pos.get(0);
        Order d = dos.get(0);
        Order diff = check(p,d);
        save(diff);
    }
    void checkAll(){
        Thread T1 = new Thread(()->{
           while(hasOrder("")) {
               pos.addAll(getPOrders());
               try {
                   barrier.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               } catch (BrokenBarrierException e) {
                   e.printStackTrace();
               }
           }
        });
        T1.start();
        Thread T2 = new Thread(()->{
            while(hasOrder("")) {
                dos.addAll(getDOrders());
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        T2.start();
    }

    private Collection<? extends Order> getDOrders() {
        return Collections.emptyList();
    }

    private Collection<? extends Order> getPOrders() {
        return Collections.emptyList();
    }

    private boolean hasOrder(String str) {
        return "".equals(str);
    }

    private void save(Order diff) {
        // doNothing
    }

    private Order check(Order p, Order d) {
        return new Order();
    }
}
