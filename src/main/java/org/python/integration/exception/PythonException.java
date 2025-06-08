package org.python.integration.exception;

import org.python.integration.object.IPythonObject;

public class PythonException extends RuntimeException {
    private final IPythonObject value;

    private PythonException(IPythonObject value) {
        this.value = value;
    }

    public IPythonObject getValue() {
        return value;
    }

    public native void free();
}
