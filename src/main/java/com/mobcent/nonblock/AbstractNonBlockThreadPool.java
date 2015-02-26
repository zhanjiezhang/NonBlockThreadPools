package com.mobcent.nonblock;

public abstract class AbstractNonBlockThreadPool implements NonBlockThreadPool {
    
    protected String threadPoolName = null;
    
    public AbstractNonBlockThreadPool() {
    }
    
    public AbstractNonBlockThreadPool(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }
    
    public String getThreadPoolName() {
        return threadPoolName;
    }
}
