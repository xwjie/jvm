# 实战JAVA虚拟机

# java语言规范

语法，词法，支持的数据类型，变量类型，数据类型转换的约定，数组，异常等，告诉开发人员“java代码应该怎么样写”

## 词法

什么样的单词是对的。

|整数可以有下划线

## 语法

什么样的语句是对的。

## 数据类型的定义

char为16位无符号整数。
float和double为满足IEEE754的32位浮点数和64位浮点数。

引用数据类型分为3重
* 类/接口
* 泛型类型
* 数组类型

## 数字编码

整数用补码表示，正数的补码是本身，负数的补码就是反码+1。反码就是符号位不变，其他位取反。

### 补码的好处
* 0既不是正数也不是负数，反码不好表示，补码则相同。
* 补码将加减法的做法完全统一，无需区分正数和负数

### 浮点数的表示

IEEE754规范，一个浮点数由符号位，指数位和尾数位3部分组成。
32位的float类型，符号位1位，指数位8位，尾数位为23位。

| s eeeeeeee m(23个）
| 当e全部为0的时候，m前面加0，否则加1


浮点数取值为 s * m * 2的（e-127）次方

-5 = 1 1000001 010（21个0）

因为 e 不全为0，前面加个1，实际尾数为 1010（21个0）

```
-5 = -1 * 2的（129-127）次方 * （1*2的0次方+0*2的-1次方+1*2的-2次方+0*2的-3次方+后面都是0）
    = -1 * 4 * （1 + 0 + 1/4 + 0 ...）
    = -1 * 4 * 1.25
    = -5
```

```java
        float f = -5;

        // float的内部表示
        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(f)));
//1  10000001(指数位8位)  01000000000000000000000（23位）
	

        double d = -5;

//1 10000000001（指数位11位） 0100000000000000000000000000000000000000000000000000（52位）
        // double 的内部表示
        System.out.println(Long.toBinaryString(Double.doubleToRawLongBits(d)));
```

# 基本结构

![基本结构](img-jvm/basic.png)

NIO可以使用直接内存（堆外内存）。

PC：如果是不是本地方法，PC指向正在执行的指令，如果是本地方法，PC就是undefined。

## 堆

![](img-jvm/heap.jpg)

## 栈

| -Xss128K 指定最大栈空间。

线程私有的内存空间。

至少包含：
* 局部变量表
* 操作数栈
* 帧数据区

函数被返回的时候，栈帧被弹出。二种方式
* 正常返回，就是return
* 异常抛出

栈深度太大，会抛出 **StackOverflowError** 栈溢出错误。


### 局部变量表

保持函数的参数和局部变量。越多越大，嵌套调用次数就会减少。

```java
    public static  void recursion(int i1, float f1, long l1, double d1){
        count++;

        // 局部变量
        int i2=1; // 1 word = 32bit
        float f2 = 2f; // 1 word
        long l2 = 3L; // 2 word
        double d2 = 4; // 2 word

        Object o1= null; // 1 word
        Object o2= null; // 1 word

        recursion(i1,f1,l1, d1);
    }
```

![占用空间](img-jvm/stack-1.png)

![局部变量表内容](img-jvm/stack-2.png)

**槽位复用**

```java
    // 复用槽位
    public static  void recursion2(int i1){
        {
            int i2 = i1;
            System.out.println(i2); // 需要使用，否则优化掉了
        }

        int i3 = 1;
    }
```

![](img-jvm/stack-3.png)

**占2word的槽位的复用**

```java
    // 局部变量表是4 word
    public static  void recursion3(double d1){
        {
            double d2 = d1; // 2 word
            System.out.println(d2);
        }

        int i3 = 1; // 1 word 复用 d2的第一个1槽位
        int i4 = 2; // 1 word 复用 d2的第二个操作
    }

```

**槽位和gc**

```java
    public static  void gc(){
        {
            byte[] a = new byte[6*1024*1024];
        }

        // a 虽然失效，但仍然在局部变量表，无法gc
        System.gc();
    }

    public static  void gc2(){
        {
            byte[] a = new byte[6*1024*1024];
        }

        int c = 1;

        // a 虽然失效，但仍然在局部变量表，无法gc
        System.gc();
    }
```

### 异常处理表

异常处理表是帧数据区重要的一部分

```java
    public static void main(String[] args) {
        try {
            recursion(1,2,3,4);
        }
        catch (Throwable e){
            System.out.println(count);
            e.printStackTrace();
        }
    }
```

**机器码如下**

![异常处理表](img-jvm/exception-table-1.png)


**异常表如下**

![异常处理表](img-jvm/exception-table-2.png)


## 栈上分配

基础的基于 **逃逸分析** （判断对象的作用域是否可能逃逸出函数体）

必须 **server模式** 。栈上分配并没有真正实现，是通过标量替换实现的。？

| -server -XX:+DoEscapeAnalysis -XX:+EliminateAllocations



**逃逸分析**
* 同步消除
* 标量替换

通过-XX:+**EliminateAllocations** 可以开启标量替换， -XX:+**PrintEliminateAllocations** 查看标量替换情况。

## 方法区

1.8之前可以理解为 **永久区**（PerSize，MaxPerSize）。1.8之后使用 **元数据区** 取代。（MaxMetaspaceSize）。






---
---
---


# 常用java虚拟机参数

## 参看参数

-XX:+PrintFlagsInitial 打印所有参数

-XX:+PrintFlagsFinal

-XX:+PrintCommmandLineFlags 打印传递的参数

-XX:+PrintVMOptions

## GC信息

PrintGC/PrintGCDetails

PrintHeapAtGC(GC前后堆信息)

-Xloggc:log/gc.log 日志存放



## 类加载、卸载

-verbose:class 跟踪类加载和卸载。等于 -XX:+TraceClassLoding 和  -XX:+TraceClassUnLoding

> verbose - 
adj.冗长的；啰嗦的；唠叨的。详细；罗嗦的；详细的

## OOM类型

* Java Heap 溢出（Java heap spacess）
* 虚拟机栈和本地方法栈溢出（StackOverflowError）
* 运行时常量池溢出（PermGen space）
* 方法区溢出 （PermGen space）
* 线程过多（unable to creat new native thread）
* GC效率低下 (GC overhead limit exceeded)
* 直接内存内存溢出 (Direct buffer memory)
* 数组过大 （Requested array size exceeds VM limit）>=Integer.MAX_VALUE-1时

## 非堆内存

-XX:MaxDirectMemorySize，如果不配置，默认为最大堆空间（-Xmx）。直接内存使用达到时候会触发GC，可能导致OOM

## 虚拟机工作模式

Client / Server /





---
---
---
# GC算法

## 引用计数器

存在循环引用和性能问题。没有采用。

## 标记清除法

标记阶段：通过根节点，标记所有可到达对象

**缺点** 回收后的空间是不连续的。

## 复制算法（新生代）

内存分2块，每次用一块。

**新生代**：分为eden，from，to 3块。from和to称为survivor区，大小一样的2块。

**老生代**

这种算法适合 **新生代** 。

## 标记压缩法（老生代）

将所有存活对象压缩到内存一端，然后直接清除边界外的空间。

## 分代算法

新生代采用复制算法，老生代采用标记压缩法/标记清除算法。

### 卡表（Card Table）

卡表中每一个位表示年老代4K的空间，卡表记录未0的年老代区域没有任何对象指向新生代，只有卡表位为1的区域才有对象包含新生代引用，因此在新生代GC时，只需要扫描卡表位为1所在的年老代空间。使用这种方式，可以大大加快新生代的回收速度。

![card table](img-jvm/card-table.png)

卡表为1的时候，才扫描对应的老生代。

新生代GC的时候，只需要扫描所有卡表为1所在的老生代空间。加快新生代GC时间。

![card table](img-jvm/card-table-2.png)

![card table](img-jvm/card-table-3.png)


## 分区算法

内存分为多个区处理。


## 可触及性

包含3种状态

* 可触及的：根节点开始可到达。
* 可复活的：对象所有引用被释放，但是对象可能在finalize函数中复活。
* 不可触及的：finalize函数已经调用，而且没有复活。

## 引用

* 强引用
* 软引用：可被回收。GC 不一定会回收，但内存紧张就会被回收，不会导致OOM。
* 弱引用：发现就回收，不够空间够不够。
* 虚引用：对象回收跟踪，必须和引用队列一起使用，作业在于跟踪垃圾回收过程。

## STW（Stop-The-World）





---
---
---
# 垃圾收集器和内存分配
















