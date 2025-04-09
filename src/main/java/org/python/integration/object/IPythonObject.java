package org.python.integration.object;

import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;

import java.util.Optional;

public interface IPythonObject {
    String representation() throws PythonException;

    IPythonObject getAttribute(String attrName) throws PythonException, NativeOperationException;

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();

    Optional<PythonBool> asBool();
}
