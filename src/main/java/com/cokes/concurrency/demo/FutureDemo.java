package com.cokes.concurrency.demo;

import java.util.concurrent.*;

public class FutureDemo {

    static ExecutorService executor = Executors.newFixedThreadPool(1);

    public void future() throws ExecutionException, InterruptedException {
        Result result = new Result();
        result.setA("123131");
        Future<Result> future = executor.submit(new Task(result),result);
        result = future.get();
        System.out.println(result.getA());
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureDemo futureDemo = new FutureDemo();
        futureDemo.future();
        executor.shutdown();
    }

}

class Result{
    private String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}

class Task implements Runnable{
    Result r;

    Task(Result r) {
        this.r = r;
    }

    @Override
    public void run() {
       String a = r.getA();
       r.setA("33333333");
        System.out.println(a);
    }
}