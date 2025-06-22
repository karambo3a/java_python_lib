package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;
import org.python.integration.object.PythonStr;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonCallableTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    @ParameterizedTest
    @MethodSource("provideDataSuccessful")
    @DisplayName("Should successfully call PythonCallable object")
    void testToJavaBooleanSuccessful(String attrName, int expected, int... args) {
        IPythonObject[] pythonArgs = new IPythonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            pythonArgs[i] = PythonInt.from(args[i]);
        }

        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr = list.getAttribute(attrName);
        Optional<PythonCallable> callableAttr = attr.asCallable();
        assertTrue(callableAttr.isPresent());

        IPythonObject result = callableAttr.get().call(pythonArgs);
        assertNotNull(result);
        assertEquals(PythonInt.from(expected), result.asInt().get());
    }

    private static Stream<Arguments> provideDataSuccessful() {
        return Stream.of(
                // Successful call without args
                Arguments.of("__len__", 3, new int[]{}),
                // Successful call with args
                Arguments.of("__getitem__", 2, new int[]{1})
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataTrows")
    @DisplayName("Should throws when calling PythonCallable object with wrong number of arguments")
    void testToJavaBooleanThrowsPythonException(String attrName, String... args) {
        IPythonObject[] pythonArgs = new IPythonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            pythonArgs[i] = PythonCore.evaluate(args[i]);
        }

        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr = list.getAttribute(attrName);
        Optional<PythonCallable> callableAttr = attr.asCallable();
        assertTrue(callableAttr.isPresent());

        PythonException exception = assertThrows(PythonException.class, () -> callableAttr.get().call(pythonArgs));
        assertNotNull(exception.getValue());

        exception.free();
    }

    private static Stream<Arguments> provideDataTrows() {
        return Stream.of(
                // Throws when calling with more args
                Arguments.of("__len__", new String[]{"[1, 2, 3]"}),
                // Throws when calling with no args
                Arguments.of("__getitem__", new String[]{})
        );
    }


    @Test
    @DisplayName("Should throws when calling on freed PythonCallable")
    void testToJavaBooleanThrowsNativeOperationException() {
        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr = list.getAttribute("__len__");
        Optional<PythonCallable> callableAttr = attr.asCallable();
        assertTrue(callableAttr.isPresent());

        IPythonObject callableAttrValue = callableAttr.get();
        PythonCore.free(callableAttrValue);
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> callableAttr.get().call());

        assertEquals("Associated Python object with Java object is NULL", exception.getMessage());
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr1 = list.getAttribute("__len__").asCallable().get();

        assertTrue(attr1.equals(attr1));
    }


    @Test
    @DisplayName("Equals should return false Java boolean (equals with the different classes)")
    void testEqualsWithDifferentClasses() {
        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr1 = list.getAttribute("__len__");

        assertNotEquals(attr1, list);
    }

    @Test
    @DisplayName("Should successfully convert Consumer to PythonCallable")
    void testFromConsumerSuccessful() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from((arg1) -> {
        });
        IPythonObject res = callable.call(integer);
        assertEquals("None", res.representation());
    }

    @Test
    @DisplayName("Should throws when converting null Consumer to PythonCallable")
    void testFromConsumerThrows() {
        Consumer<IPythonObject> consumer = null;
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCallable.from((consumer)));
        assertEquals("Java consumer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert Supplier to PythonCallable")
    void testFromSupplierSuccessful() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from(() -> integer);
        Optional<PythonInt> res = callable.call().asInt();
        assertTrue(res.isPresent());
        assertEquals(1, res.get().toJavaInt());
    }

    @Test
    @DisplayName("Should throws when converting null Supplier to PythonCallable")
    void testFromSupplierThrows() {
        Supplier<IPythonObject> supplier = null;
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCallable.from(supplier));
        assertEquals("Java supplier cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert Function to PythonCallable")
    void testFromFunctionFunctionSuccessful() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from((arg) -> integer);
        Optional<PythonInt> res = callable.call(integer).asInt();
        assertTrue(res.isPresent());
        assertEquals(1, res.get().toJavaInt());
    }

    @Test
    @DisplayName("Should throws when converting null Function to PythonCallable")
    void testFromFunctionThrows() {
        Function<IPythonObject, IPythonObject> function = null;
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCallable.from(function));
        assertEquals("Java function cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert BiFunction to PythonCallable")
    void testFromBinaryOperatorSuccessful() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from((arg1, arg2) -> integer);
        Optional<PythonInt> res = callable.call(integer, integer).asInt();
        assertTrue(res.isPresent());
        assertEquals(1, res.get().toJavaInt());
    }

    @Test
    @DisplayName("Should throws when converting null BiFunction to PythonCallable")
    void testFromBinaryOperatorThrows() {
        BiFunction<IPythonObject, IPythonObject, IPythonObject> biFunction = null;
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCallable.from(biFunction));
        assertEquals("Java bi function cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert Function3 to PythonCallable")
    void testFromFunction3Successful() {
        PythonInt integer1 = PythonInt.from(1);
        PythonInt integer2 = PythonInt.from(10);
        PythonInt integer3 = PythonInt.from(100);

        PythonCallable callable = PythonCallable.from((arg1, arg2, arg3) -> integer1);
        IPythonObject res = callable.call(integer1, integer2, integer3);
        assertEquals(integer1, res.asInt().get());
    }

    @Test
    @DisplayName("Should successfully convert Function3 to PythonCallable")
    void testFromFunction3Throws() {
        PythonCallable.Function3<IPythonObject, IPythonObject, IPythonObject, IPythonObject> function3 = null;
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCallable.from(function3));
        assertEquals("Java function3 cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert Function (returns PythonObject created inside it) to PythonCallable")
    void testFromFunctionWithInnerScope() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from((arg1) -> {
            return PythonInt.from(2);
        });
        Optional<PythonInt> res = callable.call(integer).asInt();
        assertEquals(2, res.get().toJavaLong());
    }

    @Test
    @DisplayName("Should successfully convert Function (returns PythonObject created inside inner scope) to PythonCallable")
    void testFromFunctionWithInnerPythonScope() {
        PythonInt integer = PythonInt.from(1);
        PythonCallable callable = PythonCallable.from((arg1) -> {
            try (PythonScope pythonScope = new PythonScope()) {
                return PythonInt.from(2).keepAlive();
            }
        });
        Optional<PythonInt> res = callable.call(integer).asInt();
        assertEquals(2, res.get().toJavaLong());
    }

    @Test
    @DisplayName("Should successfully call Java map om PythonList with Java method")
    void testJavaMapOnPythonListWithJavaFunction() {
        PythonList list = PythonList.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2),
                PythonInt.from(3)
        ));
        PythonList newList = PythonList.from(list.stream().map(a -> (IPythonObject) PythonStr.from("wow!")).toList());
        assertEquals(PythonList.from(List.of(
                        PythonStr.from("wow!"),
                        PythonStr.from("wow!"),
                        PythonStr.from("wow!")
                )), newList
        );
    }

    @Test
    @DisplayName("Should successfully call Python map on PythonList with Python function")
    void testPythonMapOnPythonListWithPythonFunction() {
        PythonCallable func = PythonCore.evaluate("lambda a : a + 10").asCallable().get();
        PythonList list = PythonList.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2),
                PythonInt.from(3)
        ));
        PythonCallable map = PythonCore.evaluate("map").asCallable().get();
        PythonList newList = PythonList.of(map.call(func, list));
        assertEquals(PythonList.from(List.of(
                        PythonInt.from(11),
                        PythonInt.from(12),
                        PythonInt.from(13))),
                newList
        );
    }

    @Test
    @DisplayName("Should successfully call Python map on PythonList with Java function")
    void testPythonMapOnPythonListWithJavaFunction() {
        PythonCallable func = PythonCallable.from((arg) -> {
            return PythonInt.from(10);
        });
        PythonList list = PythonList.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2),
                PythonInt.from(3)
        ));
        PythonCallable map = PythonCore.evaluate("map").asCallable().get();
        PythonList newList = PythonList.of(map.call(func, list));
        assertEquals(PythonList.from(List.of(
                        PythonInt.from(10),
                        PythonInt.from(10),
                        PythonInt.from(10))),
                newList
        );
    }

    @Test
    @DisplayName("Should successfully call Java map on PythonList with Python function")
    void testJavaMapOnPythonListWithPythonFunction() {
        PythonCallable func = PythonCore.evaluate("lambda a: 10").asCallable().get();
        PythonList list = PythonList.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2),
                PythonInt.from(3)
        ));
        PythonList newList = PythonList.from(list.stream().map(func::call).toList());
        assertEquals(PythonList.from(List.of(
                        PythonInt.from(10),
                        PythonInt.from(10),
                        PythonInt.from(10))),
                newList
        );
    }

    @ParameterizedTest
    @MethodSource("provideInputForEqualsTest")
    void testEquals(String a1, String a2, boolean expected) {
        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        PythonCallable attr1 = list.getAttribute(a1).asCallable().get();
        PythonCallable attr2 = list.getAttribute(a2).asCallable().get();

        assertEquals(expected, attr1.equals(attr2));
    }

    private static Stream<Arguments> provideInputForEqualsTest() {
        return Stream.of(
                // Should return true for equal objects
                Arguments.of("__len__", "__len__", true),
                // Should return false for unequal objects
                Arguments.of("__len__", "__str__", false)
        );
    }

    @Test
    @DisplayName("Should return the same hashCode during a single run")
    void testHashCode() {
        PythonCallable pythonCallable = PythonCore.evaluate("lambda a: 10").asCallable().get();

        int hashCode1 = pythonCallable.hashCode();
        int hashCode2 = pythonCallable.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should return the same hashCode for equal PythonCallable objects")
    void testHashCodeEqualObj() {
        PythonCallable pythonCallable1 = PythonCore.evaluate("map").asCallable().get();
        PythonCallable pythonCallable2 = PythonCore.evaluate("map").asCallable().get();

        assertEquals(pythonCallable1, pythonCallable2);
        assertEquals(pythonCallable1.hashCode(), pythonCallable2.hashCode());
    }
}
