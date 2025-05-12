package org.python.integration.object;

import java.util.Optional;

public interface IPythonObject {
    IPythonObject keepAlive();

    String representation();

    IPythonObject getAttribute(String attrName);

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();

    Optional<PythonBool> asBool();

    Optional<PythonStr> asStr();

    Optional<PythonList> asList();

    Optional<PythonDict> asDict();

    Optional<PythonTuple> asTuple();

    Optional<PythonSet> asSet();
}
