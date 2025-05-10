package org.python.integration.object;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PythonCallable extends AbstractPythonObject {

    private PythonCallable(long index, long scope) {
        super(index, scope);
    }

    public native IPythonObject call(IPythonObject... args);

    public static native PythonCallable from(Consumer<IPythonObject> consumer);

    public static native PythonCallable from(Supplier<IPythonObject> supplier);

    public static native PythonCallable from(Function<IPythonObject, IPythonObject> unaryOperator);

    public static native PythonCallable from(BiFunction<IPythonObject, IPythonObject, IPythonObject> binaryOperator);
}
