package org.python.integration.object;

/**
 * Represents a Python objects in Java.
 *
 * <p>This class is a wrapper for Python objects values.
 *
 * @see IPythonObject
 */
public class PythonObject extends AbstractPythonObject {

    PythonObject(long index, long scopeId) {
        super(index, scopeId);
    }
}
