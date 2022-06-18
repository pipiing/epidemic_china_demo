package com.chen.boot.exception;

/**
 * @author Forget_chen
 * @className UserNameNotFoundException
 * @desc
 * @date 2022/6/18 11:49
 */
public class UserNameNotFoundException extends RuntimeException {
    public UserNameNotFoundException() {
    }

    public UserNameNotFoundException(String message) {
        super(message);
    }

    public UserNameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNameNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNameNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
