package org.python.integration;

public class PythonCore {
    private PythonCore() {
    }

    public static native IPythonObject evaluate(String str);

    public static native void free(IPythonObject obj);
}
