package org.python.integration.core;

public class PythonSession implements AutoCloseable {

    private boolean isClosed;

    static {
        System.loadLibrary("native");
    }

    public PythonSession() {
        initializePy();
        this.isClosed = false;
    }

    @Override
    public void close() {
        if (!this.isClosed) {
            finalizePy();
            this.isClosed = true;
        }
    }

    private native void initializePy();

    private native void finalizePy();
}
