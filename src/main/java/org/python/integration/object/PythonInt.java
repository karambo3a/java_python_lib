package org.python.integration.object;


public class PythonInt extends AbstractPythonObject {
    private PythonInt(long index, long scope) {
        super(index, scope);
    }

    public native int toJavaInt();

    public static native PythonInt from(int value);
}
