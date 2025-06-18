package org.python.integration.object;

import org.python.integration.core.PythonScope;
import org.python.integration.exception.PythonException;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class PythonSet extends AbstractSet<IPythonObject> implements IPythonObject {
    private final IPythonObject pythonSet;
    private final long index;
    private final long scopeId;

    private PythonSet(long index, long scopeId) {
        this.index = index;
        this.scopeId = scopeId;
        this.pythonSet = new PythonObject(index, scopeId);
    }

    @Override
    public PythonSet keepAlive() {
        return this.pythonSet.keepAlive().asSet().get();
    }

    @Override
    public String representation() {
        return this.pythonSet.representation();
    }

    @Override
    public IPythonObject getAttribute(String attrName) {
        return this.pythonSet.getAttribute(attrName);
    }

    @Override
    public Optional<PythonCallable> asCallable() {
        return this.pythonSet.asCallable();
    }

    @Override
    public Optional<PythonInt> asInt() {
        return this.pythonSet.asInt();
    }

    @Override
    public Optional<PythonFloat> asFloat() {
        return this.pythonSet.asFloat();
    }

    @Override
    public Optional<PythonBool> asBool() {
        return this.pythonSet.asBool();
    }

    @Override
    public Optional<PythonStr> asStr() {
        return this.pythonSet.asStr();
    }

    @Override
    public Optional<PythonList> asList() {
        return this.pythonSet.asList();
    }

    @Override
    public Optional<PythonDict> asDict() {
        return this.pythonSet.asDict();
    }

    @Override
    public Optional<PythonTuple> asTuple() {
        return this.pythonSet.asTuple();
    }

    @Override
    public Optional<PythonSet> asSet() {
        return this.pythonSet.asSet();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IPythonObject)) {
            return false;
        }
        return pythonSet.equals(object);
    }

    @Override
    public int hashCode() {
        return pythonSet.hashCode();
    }

    @Override
    public String toString() {
        return pythonSet.toString();
    }

    @Override
    public Iterator<IPythonObject> iterator() {
        return new Iterator<>() {
            final IPythonObject pythonIterator = initIterator();
            IPythonObject next = null;
            boolean exhausted = false;

            private IPythonObject initIterator() {
                try (PythonScope pythonScope = new PythonScope()) {
                    IPythonObject iterAttr = pythonSet.getAttribute("__iter__");
                    PythonCallable iterCallable = iterAttr.asCallable()
                            .orElseThrow(() -> new IllegalStateException("__iter__ is not callable"));
                    return iterCallable.call().keepAlive();
                }
            }

            @Override
            public boolean hasNext() {
                if (exhausted) {
                    return false;
                }
                if (next != null) {
                    return true;
                }

                try (PythonScope pythonScope = new PythonScope()) {
                    IPythonObject nextAttr = pythonIterator.getAttribute("__next__");
                    PythonCallable nextCallable = nextAttr.asCallable()
                            .orElseThrow(() -> new IllegalStateException("__next__ is not callable"));
                    try {
                        next = nextCallable.call().keepAlive();
                    } catch (PythonException pe) {
                        if (pe.getValue().representation().contains("StopIteration")) {
                            exhausted = true;
                            return false;
                        }
                        throw pe;
                    }
                    return true;
                }
            }

            @Override
            public IPythonObject next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                IPythonObject item = next;
                next = null;
                return item;
            }
        };
    }

    @Override
    public int size() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject lenAttr = this.pythonSet.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ in not callable"));
            PythonInt lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("result of __len__ is not int"));
            return lenInt.toJavaInt();
        }
    }

    @Override
    public boolean add(IPythonObject object) {
        try (PythonScope pythonScope = new PythonScope()) {
            int sizeBefore = size();
            IPythonObject addAttr = this.pythonSet.getAttribute("add");
            PythonCallable addAttrCallable = addAttr.asCallable().orElseThrow(() -> new IllegalStateException("add in not callable"));
            addAttrCallable.call(object);
            return sizeBefore < size();
        }
    }

    @Override
    public boolean remove(Object object) {
        if (!(object instanceof IPythonObject pythonObject)) {
            return false;
        }
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject removeAttr = this.pythonSet.getAttribute("remove");
            PythonCallable removeAttrCallable = removeAttr.asCallable().orElseThrow(() -> new IllegalStateException("remove in not callable"));
            try {
                removeAttrCallable.call(pythonObject);
                return true;
            } catch (PythonException pe) {
                if (pe.getValue().representation().contains("KeyError")) {
                    return false;
                }
                throw pe;
            }
        }
    }

    public static native PythonSet from(Set<IPythonObject> set);
}
