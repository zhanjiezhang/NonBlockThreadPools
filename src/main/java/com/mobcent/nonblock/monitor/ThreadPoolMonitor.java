package com.mobcent.nonblock.monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ThreadPoolMonitor {
    
    private static int INIT_CACHE_CAPACITY = 20;
    private static Map<String, Map<String, ThreadPoolMonitorValue>> poolCache = new ConcurrentHashMap<String, Map<String, ThreadPoolMonitorValue>>(INIT_CACHE_CAPACITY);
//    private static Map<String, ThreadPoolMonitorValue> threadCache = new ConcurrentHashMap<String, ThreadPoolMonitorValue>(INIT_CACHE_CAPACITY);
    
    public static void register(ThreadPoolMonitorValue value) {
        
        if (null != value) {
            Map<String, ThreadPoolMonitorValue> threadCache = poolCache.get(value.getThreadPoolName());
            if (null == threadCache) {
                threadCache = new ConcurrentHashMap<String, ThreadPoolMonitorValue>(INIT_CACHE_CAPACITY);
            }
            threadCache.put(value.getThreadName(), value);
            
            poolCache.put(value.getThreadPoolName(), threadCache);
        }
    }
    
    public static void removeThread(ThreadPoolMonitorValue value) {
        
        if (null != value) {
            Map<String, ThreadPoolMonitorValue> threadCache = poolCache.get(value.getThreadPoolName());
            if (null != threadCache) {
                threadCache.remove(value.getThreadName());
            }
        }
    }
    
    public static void removeThreadPool(String threadPoolName) {
        
        if (null != threadPoolName) {
            poolCache.remove(threadPoolName);
        }
    }
    
    /**
     * 打印线程池信息
     * 
     * @return
     */
    public static String monitor() {
        
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        if (null != poolCache) {
            Iterator pools = poolCache.entrySet().iterator();
            while(pools.hasNext()) {
                sb.append("\r\n");
                Map.Entry<String, Map<String, ThreadPoolMonitorValue>> poolEntry = (Map.Entry<String, Map<String, ThreadPoolMonitorValue>>) pools.next();
                String poolName = poolEntry.getKey();
                Map<String, ThreadPoolMonitorValue> threadCache = poolEntry.getValue();
                sb.append(poolName + "[");
                if (null != threadCache) {
                    Iterator threads = threadCache.entrySet().iterator();
                    while(threads.hasNext()) {
                        sb.append("\r\n");
                        Map.Entry<String, ThreadPoolMonitorValue> threadEntry = (Map.Entry<String, ThreadPoolMonitorValue>) threads.next();
                        String threadName = threadEntry.getKey();
                        sb.append(threadName + ": ");
                        ThreadPoolMonitorValue thread = threadEntry.getValue();
                        sb.append(thread.toString());
                    }
                }
                sb.append("]");
            }
        }
        sb.append("}");
        return sb.toString();
    }
//    public static Map<String, ThreadPoolMonitorValue> getThreadPool() {
//        return threadCache;
//    }
}
