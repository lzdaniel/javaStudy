package com.lzdaniel.javastudy.thread;

public class MyRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("这是线程；" + Thread.currentThread());
    }
}
