package cn.xiaowenjie;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class GuavaFutureDemo {

    public static void main(String[] args) {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        com.google.common.util.concurrent.ListenableFuture<Object> task = service.submit(new Callable<Object>() {
            public Object call() {
                return "回调参数success";
            }
        });

        task.addListener(() -> {
            try {
                System.out.println("done," + task.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
			}            
        }, MoreExecutors.directExecutor());


        Futures.addCallback(task, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("result = " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("t = " + t);
            }
        }, MoreExecutors.directExecutor()); // MoreExecutors.newDirectExecutorService()
    }
}