package org.python.integration.exception;

/**
 * The {@code NativeOperationException} is an unchecked exception that can be thrown when an error occurs during native code operations.
 *
 * <p> This unchecked exception shows problems in native/JNI layer in native library.
 */
public class NativeOperationException extends RuntimeException {
    private NativeOperationException(String message) {
        super(message);
    }
}
