package org.python.integration;

import java.util.Optional;

public interface PythonBaseObject {
    long getIndex();
    String representation();
    PythonBaseObject getAttribute(String attrName);
    Optional<PythonCallable> asCallable();
    Optional<PythonInt> asInt();
}
