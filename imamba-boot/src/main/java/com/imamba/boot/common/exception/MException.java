package com.imamba.boot.common.exception;

public class MException extends RuntimeException {

    private static final long serialVersionUID = -6293662498600553602L;
    private IError error;
    private String extMessage;

    public MException() {
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
    }

    public MException(String message) {
        super(message);
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
    }

    public MException(String message, Throwable cause) {
        super(message, cause);
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
    }

    public MException(Throwable cause) {
        super(cause);
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        if (cause instanceof MException) {
            MException fe = (MException)cause;
            this.error = fe.getError();
            this.extMessage = fe.getMessage();
        }

    }

    public MException(IError error) {
        super(error.getErrorCode() + ":" + error.getErrorMessage());
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.error = error;
    }

    public MException(IError error, String message) {
        super(error.getErrorCode() + ":" + error.getErrorMessage());
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.error = error;
        this.extMessage = message;
    }

    public MException(Throwable cause, IError error, String message) {
        super(message, cause);
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
        this.error = error;
    }

    public MException(IError error, Throwable cause) {
        super(cause);
        this.error = MError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.error = error;
    }

    public IError getError() {
        return this.error;
    }

    public String getExtMessage() {
        return this.extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public String toString() {
        return super.toString() + ",ErrorCode : " + this.error.getErrorCode() + ", ErrorMessage : " + this.error.getErrorMessage() + ", ExtMessage : " + this.extMessage;
    }
}
