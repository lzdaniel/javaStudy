package com.lzdaniel.javastudy.thread;

public class threadTest {
    public static void main(String[] args) {
        MyRunnable instance = new MyRunnable();

        Thread thread = new Thread(instance);
        System.out.println(thread.getState());
        thread.setName("jy测试");
        thread.setDaemon(true);
        thread.start();
        System.out.println(thread.getState());


    }
}
