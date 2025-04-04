package org.python.integration.object;

import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;

import java.util.Optional;

public abstract class AbstractPythonObject implements IPythonObject {
    private final long index;

    protected AbstractPythonObject(long index) {
        this.index = index;
    }

    @Override
    public native String representation() throws PythonException;

    @Override
    public native IPythonObject getAttribute(String attrName) throws PythonException, NativeOperationException;

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();
}
