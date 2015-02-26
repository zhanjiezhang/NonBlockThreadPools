package com.mobcent.nonblock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mobcent.nonblock.monitor.ThreadPoolMonitor;
import com.mobcent.nonblock.monitor.ThreadPoolMonitorValue;

public abstract class AbstractTimeoutTask implements TimeoutTask {
    private static Log log = LogFactory.getLog(AbstractTimeoutTask.class);
	public void run() {
	    ThreadPoolMonitorValue value = new ThreadPoolMonitorValue(Thread.currentThread());
	    ThreadPoolMonitor.register(value);
	    try {
	        value.setStatus(ThreadPoolMonitorValue.THREAD_RUNNING);
            doTask();
            value.setStatus(ThreadPoolMonitorValue.THREAD_SUCCESS);
            value.setEndTime(System.currentTimeMillis());
        } catch (Exception e) {
            value.setStatus(ThreadPoolMonitorValue.THREAD_EXCEPTION);
            value.setEndTime(System.currentTimeMillis());
            log.error(Thread.currentThread().getName() + "-Exception in doTask(): " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            ThreadPoolMonitor.removeThread(value);
        }
	}
	
	public abstract void doTask() throws Exception;
}
