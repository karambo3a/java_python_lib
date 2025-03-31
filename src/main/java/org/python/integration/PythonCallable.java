package org.python.integration;

public class PythonCallable extends AbstractPythonObject {

    public PythonCallable(long index) {
        super(index);
    }

    public native PythonBaseObject call(PythonBaseObject... args);
}
