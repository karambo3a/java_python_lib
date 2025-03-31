package org.python.integration;

public class PythonSession implements AutoCloseable{

    static {
        System.loadLibrary("native");
    }

    private native void initializePy();
    private native void finalizePy();

    public PythonSession() {
        initializePy();
    }

    @Override
    public void close() {
        finalizePy();
    }
}
