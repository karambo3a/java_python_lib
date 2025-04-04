package org.python.integration.exception;

import org.python.integration.object.IPythonObject;

public class PythonException extends Throwable{
    private final IPythonObject type;
    private final IPythonObject value;
    private final IPythonObject traceback;

    public PythonException(IPythonObject type, IPythonObject value, IPythonObject traceback) {
        this.type = type;
        this.value = value;
        this.traceback = traceback;
    }


    public IPythonObject getType() {
        return type;
    }

    public IPythonObject getValue() {
        return value;
    }

    public IPythonObject getTraceback() {
        return traceback;
    }
}
