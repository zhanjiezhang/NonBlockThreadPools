package com.mobcent.nonblock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadPoolUtil {
    private static SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    public static String formateLongTime(long time) {
        return time <= 0 ? "-" : formater.format(new Date(time));
    }
    
    public static String getThreadDesc(Thread thread) {
        return thread == null ? "pool-null-thread-null" : thread.getName();
//        return thread == null ? "pool-null-thread-null" : thread.getName() + "-" + thread.getId();
    }
    
    public static String parseThreadPoolName(String pooledThreadName) {
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
    
    public static String parseThreadName(String pooledThreadName) {
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
    
    public static void main(String[] args) {
        System.out.println(parseThreadPoolName("pool-2-thread-1"));
        System.out.println(parseThreadName("pool-2-thread-1"));
    }
}
