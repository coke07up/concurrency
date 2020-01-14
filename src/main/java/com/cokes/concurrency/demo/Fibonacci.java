package com.cokes.concurrency.demo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Fibonacci extends RecursiveTask<Integer> {

    final int n;

    Fibonacci(int n){
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) return 1;

        Fibonacci f1 = new Fibonacci(n -1);
        // 创建子任务
        f1.fork();
        Fibonacci f2 = new Fibonacci(n- 2);
        //等待子任务结果，并合并结果
        return f2.compute() + f1.join();
    }

    public static void main(String[] args) {
        ForkJoinPool  forkJoinPool =  new ForkJoinPool(4);
        Fibonacci fibonacci = new Fibonacci(4);
        Integer result = forkJoinPool.invoke(fibonacci);
        System.out.println(result);
    }
}
