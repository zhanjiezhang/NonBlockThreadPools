package com.mobcent.nonblock;

public class NonBlockThreadPoolFactory {
    
    public static NonBlockThreadPool newNonBlockThreadPool() {
        return new DefaultNonBlockThreadPool();
    }
    
    public static NonBlockThreadPool newNonBlockThreadPool(String threadPoolName) {
        return new DefaultNonBlockThreadPool(threadPoolName);
    }
    
    public static NonBlockThreadPool newNonBlockThreadPool(int initThreadPoolSize) {
        return new DefaultNonBlockThreadPool(initThreadPoolSize);
    }
    
    public static NonBlockThreadPool newNonBlockThreadPool(String threadPoolName, int initThreadPoolSize) {
        return new DefaultNonBlockThreadPool(threadPoolName, initThreadPoolSize);
    }
    
    public static NonBlockThreadPool newNonBlockThreadPool(int initThreadPoolSize, int maxStatusKeepTimes) {
        return new DefaultNonBlockThreadPool(initThreadPoolSize, maxStatusKeepTimes);
    }
    
    public static NonBlockThreadPool newNonBlockThreadPool(String threadPoolName, int initThreadPoolSize, int maxStatusKeepTimes) {
        return new DefaultNonBlockThreadPool(threadPoolName, initThreadPoolSize, maxStatusKeepTimes);
    }
}
