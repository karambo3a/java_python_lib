package org.python.integration.object;

/**
 * Represents a Python objects in Java.
 *
 * <p>This class is a wrapper for Python objects values.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 */
public class PythonObject extends AbstractPythonObject {

    PythonObject(long index, long scopeId) {
        super(index, scopeId);
    }
}
