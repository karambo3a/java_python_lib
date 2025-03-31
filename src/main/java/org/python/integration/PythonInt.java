package org.python.integration;

public class PythonInt extends AbstractPythonObject{
    public PythonInt(long index) {
        super(index);
    }

    public native int toJavaInt();
}
