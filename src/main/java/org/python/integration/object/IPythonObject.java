package org.python.integration.object;

import java.util.Optional;

public interface IPythonObject {
    String representation();

    IPythonObject getAttribute(String attrName);

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();

    Optional<PythonBool> asBool();

    Optional<PythonList> asList();

    Optional<PythonDict> asDict();
}
