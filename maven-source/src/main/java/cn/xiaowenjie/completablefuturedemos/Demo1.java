package cn.xiaowenjie.completablefuturedemos;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Demo1 {

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        System.out.println("start...");
        
        new Thread(() -> {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
		}).start();

        // 模拟耗时
        Thread.sleep(1000);

        // 告知完成
        future.complete("我做完了");
    }
}