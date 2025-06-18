package org.python.integration.object;

/**
 * Represents a Python {@code bool} in Java.
 *
 * <p>This class is a wrapper for Python boolean values {@code True} or {@code False} that
 * provides methods for safe type conversions from/to Java {@code boolean} primitives and memory management.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 */
public class PythonBool extends AbstractPythonObject {

    private PythonBool(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonBool keepAlive() {
        return super.keepAlive().asBool().get();
    }

    public native boolean toJavaBoolean();

    public static native PythonBool from(boolean value);
}
