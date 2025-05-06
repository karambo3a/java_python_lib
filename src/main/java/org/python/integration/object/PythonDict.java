package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.exception.PythonException;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PythonDict extends AbstractMap<IPythonObject, IPythonObject> implements IPythonObject {
    private final IPythonObject pythonDict;
    private final long index;
    private final long scopeId;

    private PythonDict(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
        this.pythonDict = new PythonObject(index, scopeId);
    }


    @Override
    public IPythonObject keepAlive() {
        return this.pythonDict.keepAlive().asDict().get();
    }


    @Override
    public String representation() {
        return this.pythonDict.representation();
    }


    @Override
    public IPythonObject getAttribute(String attrName) {
        return this.pythonDict.getAttribute(attrName);
    }


    @Override
    public Optional<PythonCallable> asCallable() {
        return this.pythonDict.asCallable();
    }


    @Override
    public Optional<PythonInt> asInt() {
        return this.pythonDict.asInt();
    }


    @Override
    public Optional<PythonBool> asBool() {
        return this.pythonDict.asBool();
    }


    @Override
    public Optional<PythonList> asList() {
        return this.pythonDict.asList();
    }

    @Override
    public Optional<PythonDict> asDict() {
        return this.pythonDict.asDict();
    }

    @Override
    public Optional<PythonTuple> asTuple() {
        return this.pythonDict.asTuple();
    }

    @Override
    public Optional<PythonSet> asSet() {
        return this.pythonDict.asSet();
    }


    @Override
    public Set<Entry<IPythonObject, IPythonObject>> entrySet() {
        IPythonObject itemsAttr = null;
        IPythonObject dictItems = null;
        PythonList itemsList = null;
        IPythonObject first = PythonCore.evaluate("0");
        IPythonObject second = PythonCore.evaluate("1");
        Set<Entry<IPythonObject, IPythonObject>> entries = new HashSet<>();

        try {
            itemsAttr = this.pythonDict.getAttribute("items");
            PythonCallable itemsAttrCallable = itemsAttr.asCallable().orElseThrow(() -> new IllegalStateException("items is not callable"));
            dictItems = itemsAttrCallable.call();
            itemsList = PythonList.of(dictItems);

            for (IPythonObject entry : itemsList) {
                IPythonObject getAttr = entry.getAttribute("__getitem__");
                PythonCallable getAttrCallable = getAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
                try {
                    IPythonObject key = getAttrCallable.call(first);
                    IPythonObject value = getAttrCallable.call(second);
                    entries.add(new SimpleEntry<>(key, value));
                } finally {
                    PythonCore.free(entry);
                    PythonCore.free(getAttr);
                }
            }

            return entries;
        } finally {
            PythonCore.free(itemsAttr);
            PythonCore.free(dictItems);
            PythonCore.free(itemsList);
            PythonCore.free(first);
            PythonCore.free(second);
        }
    }


    @Override
    public int size() {
        IPythonObject lenAttr = null;
        PythonInt lenInt = null;
        try {
            lenAttr = this.pythonDict.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ is not callable"));
            lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("__len__ result must be int"));
            return lenInt.toJavaInt();
        } finally {
            PythonCore.free(lenAttr);
            PythonCore.free(lenInt);
        }
    }

    @Override
    public IPythonObject get(Object key) {
        if (!(key instanceof IPythonObject)) {
            return null;
        }
        IPythonObject getAttr = null;
        try {
            getAttr = this.pythonDict.getAttribute("__getitem__");
            PythonCallable getAttrCallable = getAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            try {
                return getAttrCallable.call((IPythonObject) key);
            } catch (PythonException pe) {
                if (pe.getValue().representation().contains("KeyError")) {
                    return null;
                }
                throw pe;
            }
        } finally {
            PythonCore.free(getAttr);
        }
    }

    @Override
    public IPythonObject put(IPythonObject key, IPythonObject value) {
        IPythonObject setAttr = null;
        try {
            IPythonObject prevValue = null;
            if (this.containsKey(key)) {
                prevValue = this.get(key);
            }
            setAttr = this.pythonDict.getAttribute("__setitem__");
            PythonCallable setAttrCallable = setAttr.asCallable().orElseThrow(() -> new IllegalStateException("__setitem__ is not callable"));
            setAttrCallable.call(key, value);
            return prevValue;
        } finally {
            PythonCore.free(setAttr);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof IPythonObject)) {
            return false;
        }
        IPythonObject containsAttr = null;
        PythonBool result = null;
        try {
            containsAttr = this.pythonDict.getAttribute("__contains__");
            PythonCallable containsAttrCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            result = containsAttrCallable.call((IPythonObject) key).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        } finally {
            PythonCore.free(containsAttr);
            PythonCore.free(result);
        }
    }
}





















