package org.python.integration;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index) {
        super(index);
    }

    public native IPythonObject call(IPythonObject... args);
}
