package org.python.integration.object;

import org.python.integration.core.PythonCore;

import java.util.AbstractList;
import java.util.Optional;

public class PythonList extends AbstractList<IPythonObject> implements IPythonObject {
    private final IPythonObject pythonList;
    private long index;

    private PythonList(long index) {
        this.index = index;
        this.pythonList = new PythonObject(index);
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
    public Optional<PythonBool> asBool() {
        return this.pythonList.asBool();
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
        IPythonObject setItemAttr = null;
        IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
        try {
            IPythonObject prevObject = this.get(index);
            setItemAttr = this.pythonList.getAttribute("__setitem__");
            PythonCallable setItemCallable = setItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__setitem__ is not callable"));
            setItemCallable.call(pythonIndex, object);
            return prevObject;
        } finally {
            PythonCore.free(setItemAttr);
            PythonCore.free(pythonIndex);
        }
    }


    @Override
    public void add(int index, IPythonObject object) {
        IPythonObject insertAttr = null;
        IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
        try {
            insertAttr = this.pythonList.getAttribute("insert");
            PythonCallable insertCallable = insertAttr.asCallable().orElseThrow(() -> new IllegalStateException("insert is not callable"));
            insertCallable.call(pythonIndex, object);
        } finally {
            PythonCore.free(insertAttr);
            PythonCore.free(pythonIndex);
        }
    }

    @Override
    public IPythonObject get(int index) {
        IPythonObject getItemAttr = null;
        IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
        try {
            getItemAttr = this.pythonList.getAttribute("__getitem__");
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
            lenAttr = this.pythonList.getAttribute("__len__");
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
            containsAttr = this.pythonList.getAttribute("__contains__");
            PythonCallable containsCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            result = containsCallable.call((IPythonObject) object).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        } finally {
            PythonCore.free(containsAttr);
            PythonCore.free(result);
        }
    }

    public static native PythonList of(IPythonObject object);
}
