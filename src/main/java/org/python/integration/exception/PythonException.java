package org.python.integration.exception;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;

/**
 * The {@code PythonException} is an unchecked exception that is thrown when an error occurs during Python code operations.
 *
 * <p> This unchecked exception wraps Python exceptions from the interpreter that occur when code evaluation, importing modules or object operations.
 *
 * <p> {@code PythonException} contains original Python exception object which:
 * <ul>
 *  <li> Is stored in the root scope regardless of which scope it originated from </li>
 *  <li> Should be manually freed using {@code free()} after being caught and processed </li>
 * </ul>
 *
 * @see PythonScope
 */
public class PythonException extends RuntimeException {
    private final IPythonObject value;

    private PythonException(IPythonObject value) {
        this.value = value;
    }

    public IPythonObject getValue() {
        return value;
    }

    public void free() {
        PythonCore.free(this.value);
    }
}
