package org.python.integration.object;

import java.util.Optional;

/**
 * Represents a Python objects in a Java.
 *
 * <p>This interface provides methods for interacting with Python objects, including:
 * <ul>
 *     <li>Lifecycle management</li>
 *     <li>Attribute access</li>
 *     <li>Type conversion</li>
 *     <li>String representation</li>
 * </ul>
 *
 * <p>Type conversion methods return Optional to handle type mismatches.
 *
 * @see Optional
 */
public interface IPythonObject {

    IPythonObject keepAlive();

    String representation();

    IPythonObject getAttribute(String attrName);

    Optional<PythonCallable> asCallable();

    Optional<PythonInt> asInt();

    Optional<PythonFloat> asFloat();

    Optional<PythonBool> asBool();

    Optional<PythonStr> asStr();

    Optional<PythonList> asList();

    Optional<PythonDict> asDict();

    Optional<PythonTuple> asTuple();

    Optional<PythonSet> asSet();
}
