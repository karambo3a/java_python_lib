package org.python.integration.object;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public IPythonObject keepAlive() {
        return super.keepAlive().asCallable().get();
    }

    public native IPythonObject call(IPythonObject... args);
}
