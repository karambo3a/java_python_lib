package org.python.integration;

public class PythonSession implements AutoCloseable {

    private boolean isClosed;

    static {
        System.loadLibrary("native");
    }

    private native void initializePy();

    private native void finalizePy();

    public PythonSession() {
        initializePy();
        this.isClosed = false;
    }

    @Override
    public void close() {
        if (!isClosed) {
            finalizePy();
            this.isClosed = true;
        }
    }
}
