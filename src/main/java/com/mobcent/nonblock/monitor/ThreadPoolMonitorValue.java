package com.mobcent.nonblock.monitor;

import com.mobcent.nonblock.ThreadPoolUtil;

public class ThreadPoolMonitorValue {
    
    public static String THREAD_INIT = "INIT";
    public static String THREAD_RUNNING = "RUNNING";
    public static String THREAD_SUCCESS = "SUCCESS";
    public static String THREAD_EXCEPTION = "EXCEPTION";
    
    private long startTime;
    private long endTime;
    private String threadPoolName;
    private long threadId;
    private String threadName;
    private String status;//INIT,RUNNING,SUCCESS,EXCEPTION
//    private Thread thread;
    
    public ThreadPoolMonitorValue(Thread thread) {
        this.startTime = System.currentTimeMillis();
        this.endTime = -1;
        this.threadPoolName = getThreadPoolName(thread.getName());
        this.threadId = thread.getId();
        this.threadName = getThreadName(thread.getName());
        this.status = THREAD_INIT;
    }
    
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public String getThreadPoolName() {
        return threadPoolName;
    }
    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }
    
    public String getThreadName() {
        return threadName;
    }
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
    
    public long getThreadId() {
        return threadId;
    }
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
    
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
//    public Thread getThread() {
//        return thread;
//    }
//    public void setThread(Thread thread) {
//        this.thread = thread;
//    }
    
    private String getThreadPoolName(String pooledThreadName) {
        if (null != pooledThreadName) {
            int index = -1;
            int count = 0;
            do {
                index = pooledThreadName.indexOf('-', index + 1);
                count++;
            } while (count < 2);
            if (index > -1) {
                return pooledThreadName.substring(0, index);
            } else return "pool_null";
        } else return "pool_null";
    }
    
    private String getThreadName(String pooledThreadName) {
        if (null != pooledThreadName) {
            int index = -1;
            int count = 0;
            do {
                index = pooledThreadName.indexOf('-', index + 1);
                count++;
            } while (count < 2);
            if (index > -1) {
                return pooledThreadName.substring(index + 1);
            } else return "thread_null";
        } else return "thread_null";
    }
    
    public String toString() {
        return " Start: " + ThreadPoolUtil.formateLongTime(startTime) 
             + " End: " + ThreadPoolUtil.formateLongTime(endTime) 
             + " Status: " + status;
    }
}
