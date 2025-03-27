package org.example;

public class PythonInitializer implements AutoCloseable{

    static {
        System.loadLibrary("native");
    }

    private native void initializePy();
    private native void finalizePy();

    public PythonInitializer() {
        initializePy();
    }

    @Override
    public void close() {
        finalizePy();
    }
}
