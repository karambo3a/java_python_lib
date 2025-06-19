package org.python.integration.core;

import org.python.integration.object.IPythonObject;

/**
 * The {@code PythonScope} manages the lifetime of {@link IPythonObject} instances created in it.
 * It implements {@code AutoCloseable} to ensure correct cleanup of Python objects via try-with-resources blocks.
 *
 * <p>Have hierarchical structure. During initialization of {@link PythonSession} creates the root scope.
 *
 * <p>Automatically cleans up Python objects when {@code PythonScope} closes, unless they are explicitly moved to a parent scope using {@code keepAlive()}.
 *
 * <p> Typical usage example:
 * <pre>{@code
 *  try (PythonScope pythonScope = new PythonScope()) {
 *      IPythonObject pythonObject = createPythonObject();
 *      // pythonObject will be automatically cleaned up when scope closes.
 *      // pythonObject.keepAlive() moves object to a parent scope.
 *  }
 *  }</pre>
 *
 * @see IPythonObject
 * @see PythonSession
 */
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
