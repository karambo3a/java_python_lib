package org.python.integration.object;


public class PythonInt extends AbstractPythonObject {
    private PythonInt(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public IPythonObject keepAlive() {
        return super.keepAlive().asInt().get();
    }

    public native int toJavaInt();
}
