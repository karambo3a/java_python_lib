package org.python.integration.object;

public class PythonStr extends AbstractPythonObject{
    private PythonStr(long index, long scope) {
        super(index, scope);
    }

    @Override
    public PythonStr keepAlive() {
        return super.keepAlive().asStr().get();
    }

    public native String toJavaString();

    public static native PythonStr from(String value);
}