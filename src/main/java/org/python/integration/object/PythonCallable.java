package org.python.integration.object;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index) {
        super(index);
    }

    public native IPythonObject call(IPythonObject... args);
}
