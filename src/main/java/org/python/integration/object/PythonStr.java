package org.python.integration.object;

/**
 * Represents a Python str in Java.
 *
 * <p>This class is a wrapper for Python str values that
 * provides methods for safe type conversions from/to Java {@code String} and memory management.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * <p>Uses the UTF-8 encoding for conversion with the limitations:
 * <ul>
 *      <li>Null bytes (\0) will be truncated</li>
 *      <li>Additional Unicode characters outside BMP are not fully supported</li>
 * </ul>
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 */
public class PythonStr extends AbstractPythonObject {
    private PythonStr(long index, long scope) {
        super(index, scope);
    }

    @Override
    public PythonStr keepAlive() {
        return super.keepAlive().asStr().get();
    }

    public native String toJavaString();

    public static native PythonStr from(String value);
}