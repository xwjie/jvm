package cn.xiaowenjie.completablefuturedemos;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Demo2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("start...");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "开启异步任务";
        });

        System.out.println("end..." + future.get());
    }
}