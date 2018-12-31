package cn.xiaowenjie;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

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
        // 获得一个乐观读锁
        long stamp = sl.tryOptimisticRead(); 
     
        // 将两个字段读入本地局部变量
        double currentX = x, currentY = y; 
     
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
                    // 我们显式释放读锁
                    sl.unlockRead(stamp); 
                    
                    // 显式直接进行写锁 然后再通过循环再试
                    stamp = sl.writeLock(); 
                }
            }
        } finally {
            sl.unlock(stamp); // 释放读锁或写锁
        }
    }
}
