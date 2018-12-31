# 实战java高并发程序设计

葛一鸣

> 由于大部分知识我都已经掌握，这里只记录一些关键点，加深印象。本篇笔记并不适合初学者。

# 线程

## 三种地方可能会导致指令重排：

* 编译器优化的重排序
* 指令级并行的重排序（指令级并行技术（Instruction-Level
Parallelism， ILP）来将多条指令重叠执行）
* 内存系统的重排序（处理器使用缓存和读/写缓冲区）

cpu的重排是因为cpu指令流水线的存在。

## 线程的stop函数可能导致数据不一致问题

stop会直接终止线程，并立即释放线程持有的锁。假如你代码里面获取锁然后做几个步骤，如果调用stop之后，可能里面的几个步骤只执行了其中几个就结束了！

线程退出的正确方法是 run 函数执行结束。一般加个 volatile 的标志位，其他地方修改。

或者使用中断检测，也是标志位。

## 线程的yield函数

> yield:  放弃，屈服; 生利; 退让，退位;

Thread.yield()方法作用是：暂停当前正在执行的线程对象，并执行其他线程。

> 使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。

“我已经完成一些重要的工作了，我休息一下，给你们一个机会。”

## 线程中断 interrupt

只是给线程发送一个通知，告知目标线程，有人希望你退出，至于目标线程是否退出，完全有目标线程决定。

```java
public void interrupt() // 中断线程
public boolean isInterrupted() // 判断是否被中断
public boolean interrupted() // 判断是否被中断。被清除中断状态

```

## 线程 wait 和 notify

注意不是线程的方法，而是对象的方法。是在monitor上面调用的。

> 不要调用线程对象的 wait和notify，可能会影响系统api工作。

wait： 释放监视器，然后等待监视器，拿到监视器之后再继续执行。

notify：释放监视器。但是，需要等待synchronize块结束，其他线程才能得到，不是一调用其他线程就可以得到monitor。

## 线程的join

jdk实现 while (isAlive()) { wait(0);  }

```java
public final synchronized void join(long millis)
    throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

## 线程组

统一管理，统一命名。

```java
public int activeCount()
```

## hashmap jdk8 已经修复并发时的死循环问题，只会导致数据不一致不会死循环


# jdk 并发包

## 重入锁

### 中断响应  lockInterruptibly() 解决死锁问题

### tryLock 获取不到立刻返回false / tryLock(time) 

```java
if(lock.tryLock()){

}
```

### 公平锁

### Condition 看 ArrayBlockingQueue 代码 


## 信号量

关注重要的方法和构造函数。

## 读写锁

多个读，一个写

```java
private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

private static Lock readLock = lock.readLock();
private static Lock writeLock = lock.writeLock();

```

## 倒计数器 CountDownLatch

主线程在 CountDownLatch上等待，所有准备好后继续执行

## 循环栅栏 CyclicBarrier

CyclicBarrier是执行线程内等待。CountDownLatch是线程外（主线程）等待


## LockSupport 上的重要方法

## Guava 和 RateLimiter 限流

- 漏桶算法
  - 先缓存请求到缓存区，然后固定流速留出缓存区
- 令牌桶算法
  - 缓存区缓存的不是请求，而是令牌。算法每一个单位时间产生令牌放到缓存区，处理程序拿到令牌后才能处理。
  - 没有令牌就丢弃或者等待。

```java
static RateLimiter limiter = RateLimiter.create(2);// 每秒2个令牌（处理2个请求）

for(int i = 0; i<50; i++){
    // 等待
    limiter.acquire();
    // dosomething
}


for(int i = 0; i<50; i++){
    // 丢弃
    if(limiter.tryAcquire()){
        continue;
    }
    // dosomething
}

```

## 线程池

### 重点方法

### 任务提交逻辑

- 小于coresize，提交任务分配线程执行 addWorker(command, true)
- 大于coresize，提交队列执行 workQueue.offer(command)
  - 成功，等待执行
  - 失败
    - 提交线程池   addWorker(command, false) ，是否达到max 线程
      - 没达到，分配线程执行
      - 达到：执行拒绝策略

### 四种拒绝策略

###  线程池扩展

beforeExecute、afterExecute、terminated

### 线程池的异常

如果用submit提交任务，任务异常的时候，不调用get是不会知道的

调用Execute则可以。

```java

public void execute(Runnable command)

public Future<?> submit(Runnable task)

public <T> Future<T> submit(Runnable task, T result)

public <T> Future<T> submit(Callable<T> task) 

```

### guava 的 MoreExecutors 扩展线程池

[guava异步增强——ListenableFuture](https://www.jianshu.com/p/9c57aa5e34af)

[Guava并发：ListenableFuture与RateLimiter示例](https://my.oschina.net/cloudcoder/blog/359598)


## ConurrentLinkedQueue 

## SkipList 跳表是有序的



# JMH 性能测试工具使用

# 锁优化

## AtomicStampedReference ABA 问题 

为了解决ABA问题，伟大的java为我们提供了AtomicMarkableReference和AtomicStampedReference类，为我们解决了问题


[Java多线程：AtomicReference AtomicStampedReference AtomicMarkableReference 原子更新引用类型](https://www.cnblogs.com/2015110615L/p/6749608.html)

[关于AtomicStampedReference使用的坑](https://blog.csdn.net/xybz1993/article/details/79992120)

## 数组无锁 AtmoicIntergerArray

## 普通变量享受原子操作 AtomicIntegerFieldUpdater

AtomicIntegerFieldUpdater:  基于反射的工具，可用CompareAndSet对volatile int进行原子更新:

[AtomicIntegerFieldUpdater](https://blog.csdn.net/liyantianmin/article/details/53144939)

[AtomicIntegerFieldUpdater使用](http://www.cnblogs.com/hithlb/p/4516078.html)




# 并行模式与算法

## 单例模式

内部类

## 不变模式

final class， 只有get方法，构造函数赋值。

## 生产消费者模式

使用 blockingqueue 实现

## 高性能的生产-消费者模式：无锁实现

- ConcurrentLinkedQueue
- Disruptor
  - RingBuffer, 环形，不需要head和tail，只需要一个cursor
  - 读写数据使用CAS
  - 完全内存复用，不会分配和回收内存


[高性能队列Disruptor的使用](https://www.jianshu.com/p/8473bbb556af)

```java
// 发布事件；
RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
long sequence = ringBuffer.next();//请求下一个事件序号；

try {
    LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
    long data = getEventData();//获取要通过事件传递的业务数据；
    event.set(data);
} finally{
    ringBuffer.publish(sequence);//发布事件；
}
```

## cpu cache 伪共享

## Future 模式

订单/契约

[Java多线程 - Future模式](https://www.jianshu.com/p/949d44f3d9e3)

## guava 对 future的支持

### 增加回调

[Guava包中的ListenableFuture详情解析](https://blog.csdn.net/qq496013218/article/details/77522820)

```java
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
```

[通过实例理解 JDK8 的 CompletableFuture](https://www.ibm.com/developerworks/cn/java/j-cf-of-jdk8/index.html)

## 并行

### 希尔排序

希尔排序也是一种`插入排序`，它是简单插入排序经过改进之后的一个更高效的版本，也称为`缩小增量排序`，同时该算法是冲破O(n2）的第一批算法之一。

[图解排序算法(二)之希尔排序](https://www.cnblogs.com/chengxiao/p/6104371.html)

> 由于分组，可以并行

## 准备好了通知我： NIO

## 读完了在通知我：AIO

NIO还是同步的，AIO则是异步。

## CompletableFuture

### 完成了就通知我

future.complete(结果)

```java
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
```

### 异步执行



```java
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
```


```java
// 有返回值，输入提供者
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) 
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,  Executor executor) 

// runAsync 没有返回值，传入 runnable
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) 
public static CompletableFuture<Void> runAsync(Runnable runnable)
``` 

### 流式调用

then。。。

```java
public static void main(String[] args) throws InterruptedException, ExecutionException {
    System.out.println("start...");

    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> test())
            .thenApply(s -> s + "函数接口，加个尾巴")
            .thenAccept(System.out::println);

    System.out.println("end..." + future.get());
}
```

### 异常处理

exceptionally 处理异常，返回（默认）值

```java
public class Demo4 {

    public static int test(int i) {
       return i / 0;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("start...");

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> test(2))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return -1;
                })
                .thenApply(s -> s + "函数接口，加个尾巴")
                .thenAccept(System.out::println);

        System.out.println("end..." + future.get());
    }
}
```

### 组合多个 CompletableFuture 

```java

    public <U> CompletableFuture<U> thenCompose(
        Function<? super T, ? extends CompletionStage<U>> fn) {
        return uniComposeStage(null, fn);
    }

    public <U> CompletableFuture<U> thenComposeAsync(
        Function<? super T, ? extends CompletionStage<U>> fn) {
        return uniComposeStage(asyncPool, fn);
    }

    public <U> CompletableFuture<U> thenComposeAsync(
        Function<? super T, ? extends CompletionStage<U>> fn,
        Executor executor) {
        return uniComposeStage(screenExecutor(executor), fn);
    }

    public <U,V> CompletableFuture<V> thenCombine(
        CompletionStage<? extends U> other,
        BiFunction<? super T,? super U,? extends V> fn) {
        return biApplyStage(null, other, fn);
    }

    public <U,V> CompletableFuture<V> thenCombineAsync(
        CompletionStage<? extends U> other,
        BiFunction<? super T,? super U,? extends V> fn) {
        return biApplyStage(asyncPool, other, fn);
    }

    public <U,V> CompletableFuture<V> thenCombineAsync(
        CompletionStage<? extends U> other,
        BiFunction<? super T,? super U,? extends V> fn, Executor executor) {
        return biApplyStage(screenExecutor(executor), other, fn);
    }

```

### 支持timeout （jkd9）

orTimeOut



## 读写锁的改进： StampedLock

读写锁：悲观锁，读锁会阻塞写锁。可能引起写线程的“饥饿”。

StampedLock 乐观的读策略。

先 `long stamp = sl.tryOptimisticRead()` 获取乐观读， 然后校验 `sl.validate(stamp)`， 失败则 `sl.readLock()` 获取悲观读。

```java
public class StampedLockDemo {
    private double x, y;
    
    private final StampedLock sl = new StampedLock();

    // an exclusively locked method
    void move(double deltaX, double deltaY) { 
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    // 下面看看乐观读锁案例
    // A read-only method
    double distanceFromOrigin() { 
        long stamp = sl.tryOptimisticRead(); // 获得一个乐观读锁
     
        double currentX = x, currentY = y; // 将两个字段读入本地局部变量
     
        // 检查发出乐观读锁后同时是否有其他写锁发生？
        if (!sl.validate(stamp)) { 
            // 如果没有，我们再次获得一个读悲观锁
            stamp = sl.readLock(); 
    
            try {
                currentX = x; // 将两个字段读入本地局部变量
                currentY = y; // 将两个字段读入本地局部变量
            } finally {
                sl.unlockRead(stamp);
            }
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    // 下面是悲观读锁案例
    // upgrade
    void moveIfAtOrigin(double newX, double newY) { 
        // Could instead start with optimistic, not read mode
        long stamp = sl.readLock();

        try {
            // 循环，检查当前状态是否符合
            while (x == 0.0 && y == 0.0) { 
            
                // 将读锁转为写锁
                long ws = sl.tryConvertToWriteLock(stamp); 
            
                // 这是确认转为写锁是否成功
                if (ws != 0L) { 
                    stamp = ws; // 如果成功 替换票据
                    x = newX; // 进行状态改变
                    y = newY; // 进行状态改变
                    break;
                } 
                // 如果不能成功转换为写锁
                else { 
                    sl.unlockRead(stamp); // 我们显式释放读锁
                    stamp = sl.writeLock(); // 显式直接进行写锁 然后再通过循环再试
                }
            }
        } finally {
            sl.unlock(stamp); // 释放读锁或写锁
        }
    }
}
```

[Java 8新特性探究（十）StampedLock将是解决同步问题的新宠](http://www.importnew.com/14941.html)

[Java并发编程之StampedLock锁源码探究](https://juejin.im/entry/5b28a4c46fb9a00e8a3e534a)

- CLH 锁，自旋锁
- FIFO
- 不可重入
  
## 原子类增强

### 更快的原子类：LongAdder

### LongAdder功能的增强版：LongAccumulator


## ConcurrentHashMap 的增强

### 一堆 foreach 函数

### 一堆 reduce 操作

### 条件插入 computeIfAbsent

### 一堆 search 操作

### mappingCount 

- 返回 long，size 返回 int
- 并发的时候不一定精确

### newKeySet 返回一个set



# Akka 构建高并发程序

Scala语音

## 新并发模型 actor 

基本执行单元。比喻成一个人。




