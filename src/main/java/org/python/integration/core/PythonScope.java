package org.python.integration.core;

public class PythonScope implements AutoCloseable {

    private boolean isClosed;
    private final long scopeId;

    public PythonScope() {
        this.scopeId = initializeScope();
        this.isClosed = false;
    }

    @Override
    public void close() {
        if (!this.isClosed) {
            finalizeScope();
            this.isClosed = true;
        }
    }

    private native long initializeScope();

    private native void finalizeScope();
}
