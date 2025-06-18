package org.python.integration.object;

import java.math.BigInteger;

/**
 * Represents a Python int numbers in Java.
 *
 * <p>This class is a wrapper for Python int values that
 * provides methods for safe type conversions from Java {@code long} primitives, to Java {@code int}, {@code long}, {@code BigInteger} and memory management.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * <p> For conversion to Java types, use
 * {@code int} when the Python {@code int} is known to fits in 32 bits,
 * {@code long} for 64-bit Python values, and
 * {@code BigInteger} for values greater than 64-bit or when the size is unknown.
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 */
public class PythonInt extends AbstractPythonObject {

    private PythonInt(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonInt keepAlive() {
        return super.keepAlive().asInt().get();
    }

    public int toJavaInt() {
        return Math.toIntExact(this.toJavaLong());
    }

    public native long toJavaLong();

    public native BigInteger toJavaBigInteger();

    public static native PythonInt from(long value);
}
