package org.python.integration.object;

import org.python.integration.core.PythonCore;

import java.util.AbstractList;
import java.util.Optional;

public class PythonTuple extends AbstractList<IPythonObject> implements IPythonObject{
    private final IPythonObject pythonTuple;
    private long index;

    protected PythonTuple(long index) {
        this.index = index;
        this.pythonTuple = new PythonObject(index);
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
    public Optional<PythonBool> asBool() {
        return this.pythonTuple.asBool();
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
        IPythonObject getItemAttr = null;
        IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
        try {
            getItemAttr = this.pythonTuple.getAttribute("__getitem__");
            PythonCallable getItemCallable = getItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            return getItemCallable.call(pythonIndex);
        } finally {
            PythonCore.free(getItemAttr);
            PythonCore.free(pythonIndex);
        }
    }

    @Override
    public int size() {
        IPythonObject lenAttr = null;
        PythonInt lenInt = null;
        try {
            lenAttr = this.pythonTuple.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ in not callable"));
            lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("result of __len__ is not int"));
            return lenInt.toJavaInt();
        } finally {
            PythonCore.free(lenAttr);
            PythonCore.free(lenInt);
        }
    }

    @Override
    public boolean contains(Object object) {
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        IPythonObject containsAttr = null;
        PythonBool result = null;
        try {
            containsAttr = this.pythonTuple.getAttribute("__contains__");
            PythonCallable containsCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            result = containsCallable.call((IPythonObject) object).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        } finally {
            PythonCore.free(containsAttr);
            PythonCore.free(result);
        }
    }
}
