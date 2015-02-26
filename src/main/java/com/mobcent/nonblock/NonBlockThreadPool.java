package com.mobcent.nonblock;

import java.util.List;

public interface NonBlockThreadPool {
    
    public void execute(Runnable task);
    
    public boolean canExecute();
    
    public boolean canExecuteWait();
    
    public boolean canExecuteTimeout(int timeout);
    
    public boolean isCompleted(int timeout) throws TimeoutException;
    
    public List<Runnable> shutdownForce();
    
    public List<Runnable> shutdownSafely(int minutes);
    
}
