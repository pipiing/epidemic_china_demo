package com.chen.boot.exception;

/**
 * @author Forget_chen
 * @className RegisterFailedException
 * @desc
 * @date 2022/6/18 11:40
 */
public class RegisterFailedException extends RuntimeException{
    public RegisterFailedException() {
    }

    public RegisterFailedException(String message) {
        super(message);
    }

    public RegisterFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterFailedException(Throwable cause) {
        super(cause);
    }

    public RegisterFailedException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
