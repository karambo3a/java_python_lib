package org.python.integration.exception;

public class NativeOperationException extends RuntimeException {
    public NativeOperationException(String message) {
        super(message);
    }
}
