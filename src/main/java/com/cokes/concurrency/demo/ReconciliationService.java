package com.cokes.concurrency.demo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统对账 Demo
 */
public class ReconciliationService {

    /**
     * 对账 执行缓慢。
     */
    public void reconciliation(){
        // 存在未对账订单
        while(hasReconciliationOrder("")) {
            // 查询未对账订单
            List<Order> pos = getPOrders();
            // 查询派送订单
            List<Order> dos = getDOrders();
            // 执行对账操作
            List<Order> diff = check(pos,dos);
            save(diff);
            // 差异写入差异库
        }
    }

    /**
     * 单线程reconciliation 转 多线程 reconciliationOne 优化性能
     */
    public void reconciliationOne() throws InterruptedException {
        // 存在未对账订单
        while(hasReconciliationOrder("")) {
            // 查询未对账订单
            AtomicReference<List<Order>> pos = new AtomicReference<>();
            Thread T1 = new Thread(()->{
                 pos.set(getPOrders());
            });
            // 查询派送订单
            AtomicReference<List<Order>> dos = new AtomicReference<>();
            Thread T2 = new Thread(()->{
                dos.set(getDOrders());
            });
            T1.start();
            T2.start();
            T1.join();
            T2.join();
            // 执行对账操作
            List<Order> diff = check(pos.get(),dos.get());
            save(diff);
            // 差异写入差异库
        }
    }

    final Executor executor = Executors.newFixedThreadPool(2);
    /**
     * CountDownLatch  实现线程等待
     */
    public void reconciliationTwo() throws InterruptedException {
        // 存在未对账订单
        while(hasReconciliationOrder("")) {
            CountDownLatch latch = new CountDownLatch(2);
            // 查询未对账订单
            AtomicReference<List<Order>> pos = new AtomicReference<>();
            executor.execute(()->{
                pos.set(getPOrders());
                latch.countDown();
            });
            // 查询派送订单
            AtomicReference<List<Order>> dos = new AtomicReference<>();
            executor.execute(()->{
                dos.set(getDOrders());
                latch.countDown();
            });
            // 等待两个查询操作结束
            latch.await();
            // 执行对账操作
            List<Order> diff = check(pos.get(),dos.get());
            save(diff);
            // 差异写入差异库
        }
    }

    private void save(List<Order> diff) {
        // DO NOTING
    }

    private List<Order> check(List<Order> pos, List<Order> dos) {
        return Collections.emptyList();
    }

    private List<Order> getDOrders() {
        return Collections.emptyList();
    }

    private List<Order> getPOrders() {
        return Collections.emptyList();
    }

    private boolean hasReconciliationOrder(String s) {
        return "".equals(s);
    }
}
class Order {
    private long id;
    private String price;
}

