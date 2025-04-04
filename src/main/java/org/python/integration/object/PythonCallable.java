package org.python.integration.object;

import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index) {
        super(index);
    }

    public native IPythonObject call(IPythonObject... args) throws PythonException, NativeOperationException;
}
