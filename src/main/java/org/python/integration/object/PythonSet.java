package org.python.integration.object;

import org.python.integration.core.PythonCore;
import org.python.integration.exception.PythonException;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PythonSet extends AbstractSet<IPythonObject> implements IPythonObject{
    private final IPythonObject pythonSet;
    private long index;

    private PythonSet(long index) {
        this.index = index;
        this.pythonSet = new PythonObject(index);
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
    public Optional<PythonBool> asBool() {
        return this.pythonSet.asBool();
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
    public Iterator<IPythonObject> iterator() {
        return new Iterator<>() {
            final IPythonObject pythonIterator = initIterator();
            IPythonObject next = null;
            boolean exhausted = false;

            private IPythonObject initIterator() {
                IPythonObject iterAttr = null;
                try {
                    iterAttr = pythonSet.getAttribute("__iter__");
                    PythonCallable iterCallable = iterAttr.asCallable().orElseThrow(() -> new IllegalStateException("__iter__ is not callable"));
                    return iterCallable.call();
                } finally {
                    PythonCore.free(iterAttr);
                }
            }

            @Override
            public boolean hasNext() {
                if (exhausted) return false;
                if (next != null) {
                    return true;
                }

                IPythonObject nextAttr = null;
                try {
                    nextAttr = pythonIterator.getAttribute("__next__");
                    PythonCallable nextCallable = nextAttr.asCallable()
                            .orElseThrow(() -> new IllegalStateException("__next__ is not callable"));

                    next = nextCallable.call();
                    return true;
                } catch (PythonException pe) {
                    if (pe.getValue().representation().contains("StopIteration")) {
                        exhausted = true;
                        return false;
                    }
                    throw pe;
                } finally {
                    PythonCore.free(nextAttr);
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
        IPythonObject lenAttr = null;
        PythonInt lenInt = null;
        try {
            lenAttr = this.pythonSet.getAttribute("__len__");
            PythonCallable lenAttrCallable = lenAttr.asCallable().orElseThrow(() -> new IllegalStateException("__len__ in not callable"));
            lenInt = lenAttrCallable.call().asInt().orElseThrow(() -> new IllegalStateException("result of __len__ is not int"));
            return lenInt.toJavaInt();
        } finally {
            PythonCore.free(lenAttr);
            PythonCore.free(lenInt);
        }
    }

}
