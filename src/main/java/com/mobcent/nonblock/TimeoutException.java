package com.mobcent.nonblock;

public class TimeoutException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -7277236645538681548L;

    public TimeoutException() {
        super();
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }

}
