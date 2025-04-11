package org.python.integration.core;

import org.python.integration.object.IPythonObject;

public class PythonCore {
    private PythonCore() {
    }

    public static native IPythonObject evaluate(String str);

    public static native void free(IPythonObject obj);
}
