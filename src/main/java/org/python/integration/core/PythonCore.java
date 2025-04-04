package org.python.integration.core;

import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;

public class PythonCore {
    private PythonCore() {
    }

    public static native IPythonObject evaluate(String str) throws PythonException;

    public static native void free(IPythonObject obj) throws NativeOperationException;
}
