package org.python.integration.object;

public class PythonBool extends AbstractPythonObject{

    private PythonBool(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonBool keepAlive() {
        return super.keepAlive().asBool().get();
    }

    public native boolean toJavaBoolean();

    public static native PythonBool from(boolean value);
}
