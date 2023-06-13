package com.mogen.im.common.exception;

import lombok.Data;

@Data
public class ApplicationException extends RuntimeException {

    private  int code;

    private String error;


    public ApplicationException(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public ApplicationException(ApplicationExceptionEnum exceptionEnum){
        this.code = exceptionEnum.getCode();
        this.error = exceptionEnum.getMsg();
    }

    public ApplicationException(String message, int code, String error) {
        super(message);
        this.code = code;
        this.error = error;
    }

    public ApplicationException(String message, Throwable cause, int code, String error) {
        super(message, cause);
        this.code = code;
        this.error = error;
    }

    public ApplicationException(Throwable cause, int code, String error) {
        super(cause);
        this.code = code;
        this.error = error;
    }

    public ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, String error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.error = error;
    }
}
