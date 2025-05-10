package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.exception.PythonException;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        return pythonDict.equals(object);
    }

    @Override
    public int hashCode() {
        return pythonDict.hashCode();
    }

    @Override
    public String toString() {
        return pythonDict.toString();
    }

    @Override
    public Set<Entry<IPythonObject, IPythonObject>> entrySet() {
        return new EntrySet();
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

    @Override
    public IPythonObject remove(Object object) {
        if (!(object instanceof IPythonObject key)) {
            return null;
        }
        IPythonObject popAttr = null;
        try {
            popAttr = this.pythonDict.getAttribute("pop");
            if (!this.containsKey(key)) {
                return null;
            }
            PythonCallable popAttrCallable = popAttr.asCallable().orElseThrow(() -> new IllegalStateException("pop is not callable"));
            return popAttrCallable.call(key);
        } finally {
            PythonCore.free(popAttr);
        }
    }

    private final class EntrySet extends AbstractSet<Entry<IPythonObject, IPythonObject>> {
        @Override
        public int size() {
            return PythonDict.this.size();
        }

        @Override
        public boolean contains(Object object) {
            if (!(object instanceof Entry<?, ?> entry)) {
                return false;
            }
            if (!(entry.getKey() instanceof IPythonObject key && entry.getValue() instanceof IPythonObject value)) {
                return false;
            }
            IPythonObject dictValue = PythonDict.this.get(key);
            return dictValue != null && value.representation().equals(dictValue.representation());
        }

        @Override
        public boolean remove(Object object) {
            if (!(object instanceof Entry<?, ?> entry)) {
                return false;
            }
            if (!(entry.getKey() instanceof IPythonObject key && entry.getValue() instanceof IPythonObject value)) {
                return false;
            }
            IPythonObject dictValue = PythonDict.this.get(key);
            if (dictValue != null && dictValue.representation().equals(value.representation())) {
                PythonDict.this.remove(key);
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Entry<IPythonObject, IPythonObject>> iterator() {
            return new Iterator<>() {
                private final Iterator<DictEntry> iterator = getIterator();

                private Iterator<DictEntry> getIterator() {
                    IPythonObject keysAttr = null;
                    IPythonObject keys = null;
                    try {
                        keysAttr = PythonDict.this.pythonDict.getAttribute("keys");
                        PythonCallable keysAttrCallable = keysAttr.asCallable().orElseThrow(() -> new IllegalStateException("keys is not callable"));
                        keys = keysAttrCallable.call();
                        return PythonList.of(keys).stream().map(DictEntry::new).iterator();
                    } finally {
                        PythonCore.free(keysAttr);
                        PythonCore.free(keys);
                    }
                }

                @Override
                public boolean hasNext() {
                    return this.iterator.hasNext();
                }

                @Override
                public Entry<IPythonObject, IPythonObject> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more elements in dictionary");
                    }
                    return this.iterator.next();
                }
            };
        }
    }

    private final class DictEntry implements Entry<IPythonObject, IPythonObject> {
        private final IPythonObject key;

        public DictEntry(IPythonObject key) {
            this.key = key;
        }

        @Override
        public IPythonObject getKey() {
            return this.key;
        }

        @Override
        public IPythonObject getValue() {
            return PythonDict.this.get(key);
        }

        @Override
        public IPythonObject setValue(IPythonObject value) {
            return PythonDict.this.put(key, value);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DictEntry entry)) {
                return false;
            }
            return Objects.equals(this.getKey(), entry.getKey()) &&
                    Objects.equals(this.getValue(), entry.getValue());
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() + this.getValue().hashCode();
        }
    }
}
