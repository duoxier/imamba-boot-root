package com.imamba.boot.common.exception;

public interface IError {

    String getNamespace();

    String getErrorCode();

    String getErrorMessage();
}
