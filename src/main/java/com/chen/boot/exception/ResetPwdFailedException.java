package com.chen.boot.exception;

/**
 * @author Forget_chen
 * @className ResetPwdFailedException
 * @desc
 * @date 2022/6/18 11:41
 */
public class ResetPwdFailedException extends RuntimeException {
    public ResetPwdFailedException() {
    }

    public ResetPwdFailedException(String message) {
        super(message);
    }

    public ResetPwdFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResetPwdFailedException(Throwable cause) {
        super(cause);
    }

    public ResetPwdFailedException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
