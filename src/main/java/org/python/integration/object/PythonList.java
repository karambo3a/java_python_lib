package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.exception.PythonException;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;

/**
 * Represents a Python list in Java.
 *
 * <p>This class is a wrapper for Python list that
 * provides methods for safe type conversions from Java {@code List}, memory management and modifications.
 * It extends {@code AbstractPythonObject} which provides base operations with all Python objects.
 *
 * <p>It implements {@code RandomAccess} for efficient element access and extends {@code AbstractList} to provide core {@code List<IPythonObject>} functionality.
 * All modifications through this interface directly affect Python list in the Python interpreter associated with {@code PythonList}.
 *
 * <p>The {@code equals()} guarantees all equals contract compliance only when comparing with {@code IPythonObjects} instances.
 *
 * @see IPythonObject
 * @see AbstractPythonObject
 * @see AbstractList
 */
public class PythonList extends AbstractList<IPythonObject> implements IPythonObject, RandomAccess {
    private final IPythonObject pythonList;
    private final long index;
    private final long scopeId;

    private PythonList(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
        this.pythonList = new PythonObject(index, scopeId);
    }

    @Override
    public PythonList keepAlive() {
        return this.pythonList.keepAlive().asList().get();
    }

    @Override
    public String representation() {
        return this.pythonList.representation();
    }

    @Override
    public IPythonObject getAttribute(String attrName) {
        return this.pythonList.getAttribute(attrName);
    }

    @Override
    public Optional<PythonCallable> asCallable() {
        return this.pythonList.asCallable();
    }

    @Override
    public Optional<PythonInt> asInt() {
        return this.pythonList.asInt();
    }

    @Override
    public Optional<PythonFloat> asFloat() {
        return this.pythonList.asFloat();
    }

    @Override
    public Optional<PythonBool> asBool() {
        return this.pythonList.asBool();
    }

    @Override
    public Optional<PythonStr> asStr() {
        return this.pythonList.asStr();
    }

    @Override
    public Optional<PythonList> asList() {
        return this.pythonList.asList();
    }

    @Override
    public Optional<PythonDict> asDict() {
        return this.pythonList.asDict();
    }

    @Override
    public Optional<PythonTuple> asTuple() {
        return this.pythonList.asTuple();
    }

    @Override
    public Optional<PythonSet> asSet() {
        return this.pythonList.asSet();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        return pythonList.equals(object);
    }

    @Override
    public int hashCode() {
        return pythonList.hashCode();
    }

    @Override
    public String toString() {
        return pythonList.toString();
    }

    @Override
    public IPythonObject set(int index, IPythonObject object) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject prevObject = this.get(index).keepAlive();
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            IPythonObject setItemAttr = this.pythonList.getAttribute("__setitem__");
            PythonCallable setItemCallable = setItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__setitem__ is not callable"));
            setItemCallable.call(pythonIndex, object);
            return prevObject;
        }
    }


    @Override
    public void add(int index, IPythonObject object) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            IPythonObject insertAttr = this.pythonList.getAttribute("insert");
            PythonCallable insertCallable = insertAttr.asCallable().orElseThrow(() -> new IllegalStateException("insert is not callable"));
            insertCallable.call(pythonIndex, object);
        }
    }

    @Override
    public boolean remove(Object object) {
        if (!(object instanceof IPythonObject pythonObject)) {
            return false;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject removeAttr = this.pythonList.getAttribute("remove");
            PythonCallable removeCallable = removeAttr.asCallable().orElseThrow(() -> new IllegalStateException("remove is not callable"));
            try {
                removeCallable.call(pythonObject);
                return true;
            } catch (PythonException pe) {
                if (pe.getValue().representation().contains("ValueError")) {
                    return false;
                }
                throw pe;
            }
        }
    }

    @Override
    public IPythonObject remove(int index) {
        try (PythonScope pythonScope = new PythonScope()) {
            PythonInt pythonIndex = PythonInt.from(index);
            IPythonObject popAttr = this.pythonList.getAttribute("pop");
            PythonCallable popCallable = popAttr.asCallable().orElseThrow(() -> new IllegalStateException("pop is not callable"));
            return popCallable.call(pythonIndex).keepAlive();
        }
    }

    @Override
    public IPythonObject get(int index) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            IPythonObject getItemAttr = this.pythonList.getAttribute("__getitem__");
            PythonCallable getItemCallable = getItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            return getItemCallable.call(pythonIndex).keepAlive();
        }
    }

    @Override
    public int size() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject lenAttr = this.pythonList.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ in not callable"));
            PythonInt lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("result of __len__ is not int"));
            return lenInt.toJavaInt();
        }
    }

    @Override
    public boolean contains(Object object) {
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject containsAttr = this.pythonList.getAttribute("__contains__");
            PythonCallable containsCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            PythonBool result = containsCallable.call((IPythonObject) object).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        }
    }

    public static native PythonList of(IPythonObject object);

    public static native PythonList from(List<IPythonObject> list);
}
