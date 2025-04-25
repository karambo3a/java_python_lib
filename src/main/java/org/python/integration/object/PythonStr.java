package org.python.integration.object;

public class PythonStr extends AbstractPythonObject{
    private PythonStr(long index, long scope) {
        super(index, scope);
    }

    public native String toJavaString();

}
