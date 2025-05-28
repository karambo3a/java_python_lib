package org.python.integration.object;


import java.math.BigInteger;

public class PythonInt extends AbstractPythonObject {
    private PythonInt(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonInt keepAlive() {
        return super.keepAlive().asInt().get();
    }

    public native BigInteger toJavaNumber();

    public static native PythonInt from(int value);
}
