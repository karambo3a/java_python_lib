package org.python.integration.object;

import java.util.Optional;

public interface IPythonObject {
    String representation();

    IPythonObject getAttribute(String attrName) throws Exception;

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();
}
