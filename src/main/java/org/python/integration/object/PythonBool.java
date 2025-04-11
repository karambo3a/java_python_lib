package org.python.integration.object;

public class PythonBool extends AbstractPythonObject{

    private PythonBool(long index) {
        super(index);
    }

    public native boolean toJavaBoolean();
}
