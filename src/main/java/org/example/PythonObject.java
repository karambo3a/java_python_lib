package org.example;

public class PythonObj {
    private long addr = 0L;

    public PythonObj(long addr) {
        this.addr = addr;
    }

    public long getAddr() {
        return addr;
    }
}
