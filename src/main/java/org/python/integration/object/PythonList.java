package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;

import java.util.AbstractList;
import java.util.Optional;

public class PythonList extends AbstractList<IPythonObject> implements IPythonObject {
    private final IPythonObject pythonList;
    private final long index;
    private final long scope;

    private PythonList(long index, long scope) {
        this.index = index;
        this.scope = scope;
        this.pythonList = new PythonObject(index, scope);
    }


    @Override
    public void keepAlive() {
        this.pythonList.keepAlive();
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
    public IPythonObject set(int index, IPythonObject object) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject prevObject = this.get(index);
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            IPythonObject setItemAttr = this.pythonList.getAttribute("__setitem__");
            PythonCallable setItemCallable = setItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__setitem__ is not callable"));
            setItemCallable.call(pythonIndex, object);
            prevObject.keepAlive();
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
    public IPythonObject get(int index) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject pythonIndex = PythonCore.evaluate(String.valueOf(index));
            IPythonObject getItemAttr = this.pythonList.getAttribute("__getitem__");
            PythonCallable getItemCallable = getItemAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            IPythonObject result = getItemCallable.call(pythonIndex);
            result.keepAlive();
            return result;
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
}
