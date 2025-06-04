package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;

public class PythonTuple extends AbstractList<IPythonObject> implements IPythonObject, RandomAccess {
    private final IPythonObject pythonTuple;
    private final long index;
    private final long scopeId;

    protected PythonTuple(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
        this.pythonTuple = new PythonObject(index, scopeId);
    }

    @Override
    public PythonTuple keepAlive() {
        return this.pythonTuple.keepAlive().asTuple().get();
    }

    @Override
    public String representation() {
        return this.pythonTuple.representation();
    }

    @Override
    public IPythonObject getAttribute(String attrName) {
        return this.pythonTuple.getAttribute(attrName);
    }

    @Override
    public Optional<PythonCallable> asCallable() {
        return this.pythonTuple.asCallable();
    }

    @Override
    public Optional<PythonInt> asInt() {
        return this.pythonTuple.asInt();
    }

    @Override
    public Optional<PythonFloat> asFloat() {
        return this.pythonTuple.asFloat();
    }

    @Override
    public Optional<PythonBool> asBool() {
        return this.pythonTuple.asBool();
    }

    @Override
    public Optional<PythonStr> asStr() {
        return this.pythonTuple.asStr();
    }

    @Override
    public Optional<PythonList> asList() {
        return this.pythonTuple.asList();
    }

    @Override
    public Optional<PythonDict> asDict() {
        return this.pythonTuple.asDict();
    }

    @Override
    public Optional<PythonTuple> asTuple() {
        return this.pythonTuple.asTuple();
    }

    @Override
    public Optional<PythonSet> asSet() {
        return this.pythonTuple.asSet();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        return pythonTuple.equals(object);
    }

    @Override
    public int hashCode() {
        return pythonTuple.hashCode();
    }

    @Override
    public String toString() {
        return pythonTuple.toString();
    }

    @Override
    public IPythonObject get(int index) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject getItemAttr = this.pythonTuple.getAttribute("__getitem__");
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            PythonCallable getItemCallable = getItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            return getItemCallable.call(pythonIndex).keepAlive();
        }
    }

    @Override
    public int size() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject lenAttr = this.pythonTuple.getAttribute("__len__");
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
            IPythonObject containsAttr = this.pythonTuple.getAttribute("__contains__");
            PythonCallable containsCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            PythonBool result = containsCallable.call((IPythonObject) object).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        }
    }

    public static native PythonTuple from(List<IPythonObject> tuple);
}
