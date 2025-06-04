package org.python.integration.object;

import org.python.integration.core.PythonScope;
import org.python.integration.exception.PythonException;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map;
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
    public PythonDict keepAlive() {
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
    public Optional<PythonFloat> asFloat() {
        return this.pythonDict.asFloat();
    }

    @Override
    public Optional<PythonBool> asBool() {
        return this.pythonDict.asBool();
    }

    @Override
    public Optional<PythonStr> asStr() {
        return this.pythonDict.asStr();
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
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject lenAttr = this.pythonDict.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ is not callable"));
            PythonInt lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("__len__ result must be int"));
            return lenInt.toJavaInt();
        }
    }

    @Override
    public IPythonObject get(Object key) {
        if (!(key instanceof IPythonObject)) {
            return null;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject getAttr = this.pythonDict.getAttribute("__getitem__");
            PythonCallable getAttrCallable = getAttr.asCallable().orElseThrow(() -> new IllegalStateException("__getitem__ is not callable"));
            try {
                IPythonObject value = getAttrCallable.call((IPythonObject) key);
                return value.keepAlive();
            } catch (PythonException pe) {
                if (pe.getValue().representation().contains("KeyError")) {
                    return null;
                }
                throw pe;
            }
        }
    }

    @Override
    public IPythonObject put(IPythonObject key, IPythonObject value) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject prevValue = null;
            if (this.containsKey(key)) {
                prevValue = this.get(key).keepAlive();
            }
            IPythonObject setAttr = this.pythonDict.getAttribute("__setitem__");
            PythonCallable setAttrCallable = setAttr.asCallable().orElseThrow(() -> new IllegalStateException("__setitem__ is not callable"));
            setAttrCallable.call(key, value);
            return prevValue;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof IPythonObject)) {
            return false;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject containsAttr = this.pythonDict.getAttribute("__contains__");
            PythonCallable containsAttrCallable = containsAttr.asCallable().orElseThrow(() -> new IllegalStateException("__contains__ is not callable"));
            PythonBool result = containsAttrCallable.call((IPythonObject) key).asBool().orElseThrow(() -> new IllegalStateException("__contains__ result is not bool"));
            return result.toJavaBoolean();
        }
    }

    @Override
    public IPythonObject remove(Object object) {
        if (!(object instanceof IPythonObject key)) {
            return null;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject popAttr = this.pythonDict.getAttribute("pop");
            if (!this.containsKey(key)) {
                return null;
            }
            PythonCallable popAttrCallable = popAttr.asCallable().orElseThrow(() -> new IllegalStateException("pop is not callable"));
            return popAttrCallable.call(key).keepAlive();
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
            return value.equals(dictValue);
        }

        @Override
        public boolean remove(Object object) {
            if (!(object instanceof Entry<?, ?> entry)) {
                return false;
            }
            if (!(entry.getKey() instanceof IPythonObject key && entry.getValue() instanceof IPythonObject)) {
                return false;
            }
            if (contains(entry)) {
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
                    try (PythonScope pythonScope = new PythonScope()) {
                        IPythonObject keysAttr = PythonDict.this.pythonDict.getAttribute("keys");
                        PythonCallable keysAttrCallable = keysAttr.asCallable().orElseThrow(() -> new IllegalStateException("keys is not callable"));
                        IPythonObject keys = keysAttrCallable.call();
                        return PythonList.of(keys).keepAlive().asList().get().stream().map(DictEntry::new).iterator();
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
            return this.getKey().equals(entry.getKey()) &&
                    this.getValue().equals(entry.getValue());
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() + this.getValue().hashCode();
        }
    }

    public static native PythonDict from(Map<IPythonObject, IPythonObject> map);
}
