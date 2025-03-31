package org.python.integration;

import java.util.Optional;

public abstract class AbstractPythonObject implements PythonBaseObject{
    protected long index;

    protected AbstractPythonObject(long index) {
        this.index = index;
    }

    @Override
    public long getIndex() {
        return this.index;
    }

    @Override
    public native String representation();

    @Override
    public native PythonBaseObject getAttribute(String attrName);

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();
}
