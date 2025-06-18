package org.python.integration.object;

/**
 * Represents a Python floating-point numbers in Java.
 *
 * <p>This class is a wrapper for Python float values that
 * provides methods for safe type conversions from/to Java {@code double} primitives and memory management.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * <p> The conversion uses only for Java {@code double} (64-bit floating-point)
 * to ensure full Python float compatibility, which guarantees no precision loss during conversion.
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 */
public class PythonFloat extends AbstractPythonObject {

    protected PythonFloat(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonFloat keepAlive() {
        return super.keepAlive().asFloat().get();
    }

    public native double toJavaDouble();

    public static native PythonFloat from(double value);
}
