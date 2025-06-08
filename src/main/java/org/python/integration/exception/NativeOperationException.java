package org.python.integration.exception;

public class NativeOperationException extends RuntimeException {
    private NativeOperationException(String message) {
        super(message);
    }
}
