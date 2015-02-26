package com.mobcent.nonblock;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mobcent.nonblock.monitor.ThreadPoolMonitor;

public class DefaultNonBlockThreadPool extends AbstractNonBlockThreadPool {
    
    private static Log log = LogFactory.getLog(DefaultNonBlockThreadPool.class);
    
    private static final int TIMEOUT_UNIT = 10;
    private static final int SECOND = 1000;
//    private static final int MINUTE = 60 * SECOND;
    private static final int THREAD_POOL_MIN_SIZE = 20;
    private static final int THREAD_POOL_MAX_SIZE = 20;
    private static final int THREAD_POOL_MAX_STATUS_KEEP_TIMES = 10;
    private static final int THREAD_POOL_SIZE_TIMES= 5;
    
    private ThreadPoolExecutor threadPool = null;
    private int threadPoolSize = THREAD_POOL_MIN_SIZE;
    
    // ����̳߳�״̬����������,�����ж��̳߳��Ƿ�����.
    private int maxStatusKeepTimes = THREAD_POOL_MAX_STATUS_KEEP_TIMES;
    
    // �̳߳ؼ������.
    private int activeThreadCount = 0;
    private int blockQueueSize = 0;
    private long completedTaskCount = 0;
    
    // ����߳�.
    private volatile boolean monitorStarted = false;
    private volatile boolean monitorShutdowned = false;
    private Thread monitor = null;
    
    protected DefaultNonBlockThreadPool() {
        super();
        threadPool = new ThreadPoolExecutor(THREAD_POOL_MIN_SIZE, THREAD_POOL_MAX_SIZE, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = THREAD_POOL_MIN_SIZE;
        this.maxStatusKeepTimes = THREAD_POOL_MAX_STATUS_KEEP_TIMES;
    }
    
    protected DefaultNonBlockThreadPool(String threadPoolName) {
        super(threadPoolName);
        threadPool = new ThreadPoolExecutor(THREAD_POOL_MIN_SIZE, THREAD_POOL_MAX_SIZE, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = THREAD_POOL_MIN_SIZE;
        this.maxStatusKeepTimes = THREAD_POOL_MAX_STATUS_KEEP_TIMES;
    }
    
    protected DefaultNonBlockThreadPool(int threadPoolSize) {
        super();
        threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = threadPoolSize;
        this.maxStatusKeepTimes = THREAD_POOL_MAX_STATUS_KEEP_TIMES;
    }
    
    protected DefaultNonBlockThreadPool(String threadPoolName, int threadPoolSize) {
        super(threadPoolName);
        threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = threadPoolSize;
        this.maxStatusKeepTimes = THREAD_POOL_MAX_STATUS_KEEP_TIMES;
    }
    
    protected DefaultNonBlockThreadPool(int threadPoolSize, int maxStatusKeepTimes) {
        super();
        threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = threadPoolSize;
        this.maxStatusKeepTimes = maxStatusKeepTimes;
    }
    
    protected DefaultNonBlockThreadPool(String threadPoolName, int threadPoolSize, int maxStatusKeepTimes) {
        super(threadPoolName);
        threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.threadPoolSize = threadPoolSize;
        this.maxStatusKeepTimes = maxStatusKeepTimes;
    }

    public void execute(Runnable task) {
        
        if (!monitorStarted) {
            log.info("Block monitor thread start...");
            monitorStarted = true;
            monitor = new Thread(new NonBlockMonitor());
            monitor.start();
        } else {
            log.debug("Block monitor thread has started.");
        }
        
        //set thread pool's name
        if (null == threadPoolName) {
            Future<String> future = threadPool.submit(new Callable<String>() {
                public String call() throws Exception {
                    return ThreadPoolUtil.parseThreadPoolName(Thread.currentThread().getName());
                }
            });
            try {
                threadPoolName = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            log.info("Set Thread Pool Name: [" + threadPoolName + "]");
        }
        //submit task.
        threadPool.execute(task);
    }
    
    /**
     * �ж��̳߳��Ƿ�����.(�����̳߳�����,MAX_BLOCK_TIMES���ȴ������̳߳�״̬����һ��)
     * 
     * @param isBlocked
     * @return
     * @throws InterruptedException
     */
    private boolean isThreadPoolBlocked() throws InterruptedException {
        boolean isBlocked = false;
        int blockTimes = 0;
        // �ж��̳߳��Ƿ�����
        while (true) {
            if (activeThreadCount == threadPool.getActiveCount() 
                    && completedTaskCount == threadPool.getCompletedTaskCount()
                    && blockQueueSize == threadPool.getQueue().size() 
                    && 0 != threadPool.getActiveCount()) {// �ж�Ϊ����
                if (blockTimes == (maxStatusKeepTimes * 6)) {
                    isBlocked = true;
                    break;
                }
                Thread.sleep(TIMEOUT_UNIT * SECOND);
                blockTimes++;
            } else {
                activeThreadCount = threadPool.getActiveCount();
                completedTaskCount = threadPool.getCompletedTaskCount();
                blockQueueSize = threadPool.getQueue().size();
                break;
            }
        }
        
        return isBlocked;
    }
    
    private class NonBlockMonitor implements Runnable {
        public void run() {
            while(!monitorShutdowned) {
                log.debug(threadPoolStatus());
                try {
                    boolean isBlocked = isThreadPoolBlocked();
                    if (isBlocked) {
                        log.info("Thread Pool[" + threadPoolName + "] be blocked, shut down now and new.");
                        log.info(threadPoolStatus());
                        log.info(ThreadPoolMonitor.monitor());
                        threadPool.shutdownNow();
                        threadPool = null;
                        ThreadPoolMonitor.removeThreadPool(threadPoolName);
                        log.info(ThreadPoolMonitor.monitor());
                        threadPool = newThreadPool();
                        if (null != threadPool) {
                            log.info("New Thread Pool created [successfully].");
                            log.info("New Thread Pool's status: " + threadPoolStatus());
                            log.info("Monitor Alive: " + !monitorShutdowned);
                        } else {
                            log.info("New Thread Pool created [Unsuccessfully].");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.info("Monitor Thread is interrupted.");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("Exception occurs: " + e.getMessage());
                }
            }
            
            log.info("Monitor Thread Completed.");
        }
    }
    
    /**
     * �����µ��̳߳�.
     * 
     * @return ThreadPoolExecutor
     */
    private ThreadPoolExecutor newThreadPool() {

        ThreadPoolExecutor tp = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.activeThreadCount = 0;
        this.blockQueueSize = 0;
        this.completedTaskCount = 0;
        this.threadPoolName = null;
        return tp;
    }

    /**
     * Ϊ�����̳߳ؼ�ѹ,�������������֮ǰ�����жϿɷ����̳߳����ύ����.���ȴ���������.
     */
    public boolean canExecute() {
        if (this.threadPool.getActiveCount() < threadPoolSize || this.threadPool.getQueue().size() < (threadPoolSize * THREAD_POOL_SIZE_TIMES)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Ϊ�����̳߳ؼ�ѹ,�������������֮ǰ�����жϿɷ����̳߳����ύ����.���޵ȴ�ֱ������.
     */
    public boolean canExecuteWait() {
        while(true) {
//            if (this.threadPool.getActiveCount() == 0 || this.threadPool.getQueue().size() < threadPoolSize) {
//                return true;
//            }
            if (this.threadPool.getActiveCount() < threadPoolSize || this.threadPool.getQueue().size() < (threadPoolSize * THREAD_POOL_SIZE_TIMES)) {
                return true;
            }
            try {
//                Thread.sleep(1 * MINUTE);
                Thread.sleep(TIMEOUT_UNIT * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.warn("canExecuteWait: Interrupted.", e);
            }
        }
    }
    
    /**
     * Ϊ�����̳߳ؼ�ѹ,�������������֮ǰ�����жϿɷ����̳߳����ύ����.���ȴ�minutes����.
     * 
     * @param minutes minutesС�ڵ���0ʱ��������.
     * @see canExecute()
     */
    public boolean canExecuteTimeout(int minutes) {
        
        if (this.threadPool.getActiveCount() < threadPoolSize || this.threadPool.getQueue().size() < (threadPoolSize * THREAD_POOL_SIZE_TIMES)) {
            return true;
        }
        for(int i = 0; i < (minutes * 6); i++) {
            try {
                Thread.sleep(TIMEOUT_UNIT * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.warn("canExecuteTimeout: Interrupted.", e);
            }
            if (this.threadPool.getActiveCount() < threadPoolSize || this.threadPool.getQueue().size() < (threadPoolSize * THREAD_POOL_SIZE_TIMES)) {
                return true;
            }
        }
        
        if (this.threadPool.getActiveCount() < threadPoolSize || this.threadPool.getQueue().size() < (threadPoolSize * THREAD_POOL_SIZE_TIMES)) {
            return true;
        } return false;
    }
    
    /**
     * �̳߳��������Ƿ����.
     * 
     * @param timeout ���ȴ�timeout����.
     * @author zhangjun
     * @throws TimeoutException 
     */
    public boolean isCompleted(int timeout) throws TimeoutException {
        
        if (0 == this.threadPool.getActiveCount()) {
            return true;
        }
        
        for (int i = 0; i < (timeout * 6); i++) {
            try {
                Thread.sleep(TIMEOUT_UNIT * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.warn("isCompleted: Interrupted.", e);
            }
            if (0 == this.threadPool.getActiveCount()) {
                return true;
            }
        }
        log.warn("isCompleted: timeout, throw Exception.");
        throw new TimeoutException("Timout: [" + timeout + "]min.");
    }
    
    /**
     * ǿ�ƹر��̳߳�.
     * 
     * @author zhangjun
     */
    public List<Runnable> shutdownForce() {
        
        if (null != this.monitor) {
            this.monitorShutdowned = true;
            this.monitor.interrupt();
        }
        if (null != this.threadPool) {
            return threadPool.shutdownNow();
        } else return null;
    }
    
    /**
     * �ȴ��߳�ִ����ɲ���ȫ�ر��̳߳�.
     * 
     * @author zhangjun
     */
    public List<Runnable> shutdownSafely(int minutes) {
        
        if (0 == this.threadPool.getActiveCount()) {
            return shutdownForce();
        }
        for (int i = 0; i < (minutes * 6); i++) {
            try {
                Thread.sleep(TIMEOUT_UNIT * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.warn("shutdownSafely: Interrupted.", e);
            }
            if (0 == this.threadPool.getActiveCount()) {
                return shutdownForce();
            }
        }
        
        log.warn("shutdownSafely: timeout, shutdown non-block thread pool by force.");
        //��ʱǿ�йر�.
        return shutdownForce();
    }
    
    /**
     * Thread pool's status.
     * 
     * @return
     */
    private String threadPoolStatus() {
        return "Thread Pool Status: Active: " + threadPool.getActiveCount() + ", " 
             + "Completed: " + threadPool.getCompletedTaskCount() + ", " 
             + "Queue Size: " + threadPool.getQueue().size();
    }
}
