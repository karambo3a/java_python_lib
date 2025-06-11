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

    public int toJavaInt() {
        return Math.toIntExact(this.toJavaLong());
    }

    public native long toJavaLong();

    public native BigInteger toJavaBigInteger();

    public static native PythonInt from(long value);
}
