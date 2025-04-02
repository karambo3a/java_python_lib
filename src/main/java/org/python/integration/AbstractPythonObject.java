package org.python.integration;

import java.util.Optional;

public abstract class AbstractPythonObject implements IPythonObject {
    private final long index;

    protected AbstractPythonObject(long index) {
        this.index = index;
    }

    @Override
    public native String representation();

    @Override
    public native IPythonObject getAttribute(String attrName) throws Exception;

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();
}
