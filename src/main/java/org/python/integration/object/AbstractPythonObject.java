package org.python.integration.object;

import java.util.Optional;

public abstract class AbstractPythonObject implements IPythonObject {
    private final long index;

    protected AbstractPythonObject(long index) {
        this.index = index;
    }

    @Override
    public native String representation();

    @Override
    public native IPythonObject getAttribute(String attrName);

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();

    @Override
    public native Optional<PythonBool> asBool();

    @Override
    public native Optional<PythonList> asList();

    @Override
    public native Optional<PythonDict> asDict();

    @Override
    public native Optional<PythonTuple> asTuple();

    @Override
    public native Optional<PythonSet> asSet();
}
