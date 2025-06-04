package org.python.integration.object;

public class PythonFloat extends AbstractPythonObject {

    protected PythonFloat(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonFloat keepAlive() {
        return super.keepAlive().asFloat().get();
    }

    public native double toJavaDouble();

    public static native PythonFloat from(double value);
}
