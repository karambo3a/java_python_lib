package org.python.integration.object;

public class PythonInt extends AbstractPythonObject {
    private PythonInt(long index) {
        super(index);
    }

    public native int toJavaInt();
}
