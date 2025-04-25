package org.python.integration.object;

public class PythonBool extends AbstractPythonObject{

    private PythonBool(long index, long scope) {
        super(index, scope);
    }

    public native boolean toJavaBoolean();

    public static native PythonBool from(boolean value);
}
