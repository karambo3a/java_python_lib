package org.python.integration.core;

import org.python.integration.object.IPythonObject;

import java.util.Map;

/**
 * The {@code PythonCore} provides core functionality for Python interacting with Java.
 *
 * <p>This class enables:
 * <ul>
 *  <li>Execution of Python code from Java strings</li>
 *  <li>Importing Python modules</li>
 *  <li>Manual lifecycle management of Python objects</li>
 * </ul>
 */
public class PythonCore {
    private PythonCore() {
    }

    public static native IPythonObject evaluate(String str);

    public static native void free(IPythonObject obj);

    public static native IPythonObject importModule(String module);

    public static native Map<String, IPythonObject> fromImport(String from, String... names);

    public static IPythonObject fromImportOne(String from, String name) {
        return fromImport(from, name).get(name);
    }
}
