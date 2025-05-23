package org.python.integration.object;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index, long scopeId) {
        super(index, scopeId);
    }

    @Override
    public PythonCallable keepAlive() {
        return super.keepAlive().asCallable().get();
    }

    public native IPythonObject call(IPythonObject... args);

    public static native PythonCallable from(Consumer<IPythonObject> consumer);

    public static native PythonCallable from(Supplier<IPythonObject> supplier);

    public static native PythonCallable from(Function<IPythonObject, IPythonObject> unaryOperator);

    public static native PythonCallable from(BiFunction<IPythonObject, IPythonObject, IPythonObject> binaryOperator);

    public static native PythonCallable from(Function3<IPythonObject, IPythonObject, IPythonObject, IPythonObject> function3);


    @FunctionalInterface
    public interface Function3<T, R, U, V> {
        public V apply(T t, R r, U u);
    }
}
