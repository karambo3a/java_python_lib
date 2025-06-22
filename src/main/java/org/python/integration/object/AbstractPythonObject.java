package org.python.integration.object;

import org.python.integration.core.PythonSession;
import org.python.integration.exception.PythonException;

import java.util.Optional;

/**
 * Abstract base class that implements {@code IPythonObject} with native (JNI) methods.
 *
 * <p>This class is a base class for all Python objects wrappers, providing default native implementation for core objects operations such as:
 * <ul>
 *     <li>Lifetime management</li>
 *     <li>Attribute access</li>
 *     <li>Type conversions</li>
 *     <li>String representations</li>
 * </ul>
 *
 * <p>All inheritor must be instantiated within an active {@code PythonSession}.
 *
 * @see IPythonObject
 * @see PythonSession
 */
public abstract class AbstractPythonObject implements IPythonObject {
    private final long index;
    private final long scopeId;

    protected AbstractPythonObject(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
    }

    @Override
    public native IPythonObject keepAlive();

    /**
     * Returns {@code true} if the arguments are equal to each other and {@code false} otherwise.
     * If argument is null returns null.
     *
     * <p>The equality comparison complies all equals contracts only for IPythonObjects instances.
     *
     * <p> Example for PythonList:
     * <pre>{@code
     *      PythonInt pythonInt = PythonInt.from(1);
     *      PythonList pythonList1 = PythonList.from(List.of(pythonInt));
     *      List<IPythonObject> pythonList2 = PythonList.from(List.of(pythonInt));
     *      List<IPythonObject> javaList = List.of(pythonInt);
     *
     *      // returns true because pythonList1 extends AbstractList
     *      javaList.equals(pythonList1)
     *
     *      // returns false because javaList is not instance of IPythonObject
     *      pythonList1.equals(javaList)
     *
     *      // returns true because pythonList2 is instance of IPythonObject
     *      pythonList1.equals(pythonList2)
     * }</pre>
     *
     * <p><b>Important:</b> use equals only with IPythonObject instances.
     *
     * @param object the reference object with which to compare
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise
     * @see IPythonObject
     */
    @Override
    public native boolean equals(Object object);

    /**
     * Returns a hash code value for the IPythonObject object as computed by the Python interpreter.
     *
     * <p>This method delegates to Python's {@code hash()} method and guarantees that equals Python objects produce the same hash codes.
     *
     * <p>Some of Python objets are not hashable (sush as list) and this method will throw PythonException.
     *
     * @return a hash code value for this object
     * @throws PythonException when calling on unhashable Python objects
     * @see IPythonObject
     */
    @Override
    public native int hashCode();

    @Override
    public native String toString();

    @Override
    public native String representation();

    @Override
    public native IPythonObject getAttribute(String attrName);

    @Override
    public native Optional<PythonCallable> asCallable();

    @Override
    public native Optional<PythonInt> asInt();

    @Override
    public native Optional<PythonFloat> asFloat();

    @Override
    public native Optional<PythonBool> asBool();

    @Override
    public native Optional<PythonStr> asStr();

    @Override
    public native Optional<PythonList> asList();

    @Override
    public native Optional<PythonDict> asDict();

    @Override
    public native Optional<PythonTuple> asTuple();

    @Override
    public native Optional<PythonSet> asSet();
}
