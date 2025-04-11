package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonCallableTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
    }


    @ParameterizedTest
    @MethodSource("provideDataSuccessful")
    @DisplayName("Should successfully call Python callable object")
    void testToJavaBooleanSuccessful(String attrName, String expected, String... args) {
        IPythonObject[] pythonArgs = new IPythonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            pythonArgs[i] = PythonCore.evaluate(args[i]);
        }

        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr = list.getAttribute(attrName);
        Optional<PythonCallable> callableAttr = attr.asCallable();
        assertTrue(callableAttr.isPresent());

        IPythonObject result = callableAttr.get().call(pythonArgs);
        assertNotNull(result);
        assertEquals(expected, result.representation());
    }


    private static Stream<Arguments> provideDataSuccessful() {
        return Stream.of(
                // Successful call without args
                Arguments.of("__len__", "3", new String[]{}),
                // Successful call with args
                Arguments.of("__getitem__", "2", new String[]{"1"})
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataTrows")
    @DisplayName("Should throws PythonException when calling Python callable object with wrong number of arguments")
    void testToJavaBooleanThrows(String attrName, String expected, String... args) {
        IPythonObject[] pythonArgs = new IPythonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            pythonArgs[i] = PythonCore.evaluate(args[i]);
        }

        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");
        IPythonObject attr = list.getAttribute(attrName);
        Optional<PythonCallable> callableAttr = attr.asCallable();
        assertTrue(callableAttr.isPresent());

        PythonException exception = assertThrows(PythonException.class, () -> callableAttr.get().call(pythonArgs));

        assertNotNull(exception);
        assertNotNull(exception.getType());
        assertNotNull(exception.getValue());
        assertNull(exception.getTraceback());
    }


    private static Stream<Arguments> provideDataTrows() {
        return Stream.of(
                Arguments.of("__len__", "3", new String[]{"[1, 2, 3]"}),
                Arguments.of("__getitem__", "2", new String[]{})
        );
    }

}
