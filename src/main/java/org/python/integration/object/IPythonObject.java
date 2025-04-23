package org.python.integration.object;

import java.util.Optional;

public interface IPythonObject {
    void keepAlive();

    String representation();

    IPythonObject getAttribute(String attrName);

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();

    Optional<PythonBool> asBool();

    Optional<PythonList> asList();

    Optional<PythonDict> asDict();

    Optional<PythonTuple> asTuple();

    Optional<PythonSet> asSet();
}
