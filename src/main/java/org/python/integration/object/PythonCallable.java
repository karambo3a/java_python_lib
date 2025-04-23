package org.python.integration.object;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index, long scope) {
        super(index, scope);
    }

    public native IPythonObject call(IPythonObject... args);
}
