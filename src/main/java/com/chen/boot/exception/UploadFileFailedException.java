package com.chen.boot.exception;

/**
 * @author Forget_chen
 * @className UploadFileFailedException
 * @desc
 * @date 2022/6/18 11:41
 */
public class UploadFileFailedException extends RuntimeException {
    public UploadFileFailedException() {
    }

    public UploadFileFailedException(String message) {
        super(message);
    }

    public UploadFileFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFileFailedException(Throwable cause) {
        super(cause);
    }

    public UploadFileFailedException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
