package org.python.integration.core;

public class PythonScope implements AutoCloseable {

    private boolean isClosed;

    public PythonScope() {
        initializeScope();
        this.isClosed = false;
    }

    @Override
    public void close() {
        if (!this.isClosed) {
            finalizeScope();
            this.isClosed = true;
        }
    }

    private native void initializeScope();

    private native void finalizeScope();
}
