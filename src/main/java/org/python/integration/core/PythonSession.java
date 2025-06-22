package org.python.integration.core;

/**
 * The {@code PythonSession} class handles initialization and finalization of the Python interpreter.
 * It implements {@code AutoCloseable} to ensure correct cleanup via try-with-resources blocks.
 * Open session is needed to create Python objects and their Java wrappers.
 *
 * <p> Only one {@code PythonSession} can be active at a time. Opening another session will throw an exception.
 *
 * <p>Typical usage pattern:
 * <pre>{@code
 *  try (PythonSession pythonSession = new PythonSession()) {
 *      // Work with Python objects here
 *      // The interpreter will automatically finalizes when the block ends
 *  }
 *  }</pre>
 *
 * <p><b>Important:</b> The native library "native" must be in library path.
 */
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
