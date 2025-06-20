package org.python.integration.object;

import java.util.Optional;

public abstract class AbstractPythonObject implements IPythonObject {
    private final long index;
    private final long scopeId;

    protected AbstractPythonObject(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
    }

    @Override
    public native IPythonObject keepAlive();

    @Override
    public native boolean equals(Object object);

    @Override
    public native int hashCode();

    @Override
    public native String toString();

    @Override
    public native String representation();

    @Override
    public native IPythonObject getAttribute(String attrName);

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();

    @Override
    public native Optional<PythonFloat> asFloat();

    @Override
    public native Optional<PythonBool> asBool();

    @Override
    public native Optional<PythonStr> asStr();

    @Override
    public native Optional<PythonList> asList();

    @Override
    public native Optional<PythonDict> asDict();

    @Override
    public native Optional<PythonTuple> asTuple();

    @Override
    public native Optional<PythonSet> asSet();
}
