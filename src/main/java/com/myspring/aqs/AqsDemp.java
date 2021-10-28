package com.myspring.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author clearlove
 * @ClassName AqsDemp.java
 * @Description
 * @createTime 2021年10月26日 23:27:00
 */
public class AqsDemp {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        //模拟三个线程办理业务

        //a是第一个顾客 没有任何人 直接去办
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("a come");
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "a").start();

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("b come");
            } finally {
                lock.unlock();
            }
        }, "b").start();

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("c come");
            } finally {
                lock.unlock();
            }
        }, "c").start();
    }
}
