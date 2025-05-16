package org.python.integration.core;

import org.python.integration.object.IPythonObject;

import java.util.Map;

public class PythonCore {
    private PythonCore() {
    }

    public static native IPythonObject evaluate(String str);

    public static native void free(IPythonObject obj);

    public static native IPythonObject importModule(String module);

    public static native Map<String, IPythonObject> fromImport(String from, String... names);
}
