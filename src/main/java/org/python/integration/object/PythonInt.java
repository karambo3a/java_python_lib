package org.python.integration.object;


import org.python.integration.exception.NativeOperationException;

public class PythonInt extends AbstractPythonObject {
    private PythonInt(long index) {
        super(index);
    }

    public native int toJavaInt() throws NativeOperationException;
}
