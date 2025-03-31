package org.python.integration;

public class PythonCore {
    private PythonCore(){}

    public static native PythonBaseObject evaluate(String str);

    public static native void free(PythonBaseObject obj);
}
