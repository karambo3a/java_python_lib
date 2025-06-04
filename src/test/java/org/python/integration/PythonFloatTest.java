package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonFloat;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonFloatTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    @Test
    @DisplayName("Should return correct Java double from PythonFloat")
    void testToJavaNumberSuccessful() {
        IPythonObject pythonFloat = PythonCore.evaluate("1.0");

        Optional<PythonFloat> pythonFloatOp = pythonFloat.asFloat();
        assertTrue(pythonFloatOp.isPresent());
        assertEquals(1.0, pythonFloatOp.get().toJavaDouble());
    }

    @ParameterizedTest
    @MethodSource("provideInputForEqualsTest")
    public void testEquals(String value1, String value2, boolean expected) {
        IPythonObject object1 = PythonCore.evaluate(value1);
        IPythonObject object2 = PythonCore.evaluate(value2);

        assertEquals(expected, object1.equals(object2));
    }

    private static Stream<Arguments> provideInputForEqualsTest() {
        return Stream.of(
                // Should return true for equal objects
                Arguments.of("1.0", "1.0", true),
                // Should return false for unequal objects
                Arguments.of("1.0", "2.0", false),
                // Should return false for objects of different classes
                Arguments.of("1.0", "[1.0,2.0,3.0]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject object = PythonCore.evaluate("1.0");

        assertEquals(object, object);
    }

    @Test
    @DisplayName("Should return new PythonFloat from Java double")
    void testFromJavaDouble() {
        PythonFloat pythonFloat1 = PythonFloat.from(5.0);
        assertNotNull(pythonFloat1);
        assertEquals(5.0, pythonFloat1.toJavaDouble());

        PythonFloat pythonFloat2 = PythonFloat.from(1000.0);
        assertNotNull(pythonFloat2);
        assertEquals(1000.0, pythonFloat2.toJavaDouble());
    }
}
