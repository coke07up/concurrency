package com.cokes.concurrency.demo;

import java.util.concurrent.*;

/**
 * 询价
 */
public class CompletionServiceDemo {

    /**
     * 询价应用，这个应用需要从三个电商询价，
     * 然后保存在自己的数据库里。
     * 核心示例代码如下所示，由于是串行的，所以性能很慢
     */
    public void priceOne() {
        Double r1 = getPriceByS1();
        save(r1);
        // 向电商S2询价，并保存
        Double r2 = getPriceByS2();
        save(r2);
        //向电商S3询价，并保存
        Double r3 = getPriceByS3();
        save(r3);
    }

    // 创建线程池
    ExecutorService executor = Executors.newFixedThreadPool(3);
    /**
     * 优化后
     * 用三个线程异步执行询价，
     * 通过三次调用 Future 的 get()
     * 方法获取询价结果，之后将询价结果保存在数据库中。
     */
    public void priceTwo() throws ExecutionException, InterruptedException {
        // 异步向电商S1询价
        Future<Double> f1 = executor.submit(()->
                getPriceByS1());
        Future<Double> f2 = executor.submit(()->
                getPriceByS2());
        Future<Double> f3 = executor.submit(()->
                getPriceByS3());
        // 获取电商S1报价并保存
        Double price  = f1.get();
        Double finalPrice = price;
        executor.execute(()->save(finalPrice));

        // 获取电商S2报价并保存
        price = f2.get();
        Double finalPrice1 = price;
        executor.execute(()->save(finalPrice1));

        price = f3.get();
        Double finalPrice2 = price;
        executor.execute(()->save(finalPrice2));
    }

    /**
     * 利用阻塞队列实现先获取到的报价先保存到数据库。
     */
    public  void priceThree() throws ExecutionException, InterruptedException {

        // 异步向电商S1询价
        Future<Double> f1 = executor.submit(()->
                getPriceByS1());
        Future<Double> f2 = executor.submit(()->
                getPriceByS2());
        Future<Double> f3 = executor.submit(()->
                getPriceByS3());
        // 创建阻塞队列
        BlockingQueue<Double> bq = new LinkedBlockingQueue<>();
        // 获取电商S1报价并保存
        executor.execute(()-> {
            try {
                bq.put(f1.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 获取电商S2报价并保存
        executor.execute(()-> {
            try {
                bq.put(f2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //电商S3报价异步进入阻塞队列
        executor.execute(()-> {
            try {
                bq.put(f3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //异步保存所有报价
        for (int i=0; i<3; i++) {
            Double r = bq.take();
            executor.execute(()->save(r));
        }

    }

    /**
     * CompletionService 实现异步调用
     */
    private void priceTour() throws InterruptedException, ExecutionException {
        CompletionService<Double> cs = new ExecutorCompletionService<>(executor);

        // 异步向电商S1询价
        cs.submit(()->getPriceByS1());
        // 异步向电商S2询价
        cs.submit(()->getPriceByS2());
        // 异步向电商S3询价
        cs.submit(()->getPriceByS3());
        for (int i=0; i<3; i++) {
            Double r = cs.take().get();
            executor.execute(()->save(r));
        }
    }

    private Double getPriceByS3() {
        return 10D;
    }

    private Double getPriceByS2() {
        return 11D;
    }

    private Double getPriceByS1() {
        return 2D;
    }

    private void save(Double r2) {
        // doNothing
    }

}
