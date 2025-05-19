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
import org.python.integration.object.PythonBool;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonBoolTest {
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
    @DisplayName("Should return Java boolean from PythonBool")
    void testToJavaBooleanSuccessful() {
        IPythonObject trueBool = PythonCore.evaluate("True");
        IPythonObject falseBool = PythonCore.evaluate("False");

        assertTrue(trueBool.asBool().get().toJavaBoolean());
        assertFalse(falseBool.asBool().get().toJavaBoolean());
    }


    @Test
    @DisplayName("Should return PythonBool from Java boolean")
    void testFromJavaBooleanSuccessful() {
        PythonBool pythonBool = PythonBool.from(true);

        assertNotNull(pythonBool);
        assertTrue(pythonBool.toJavaBoolean());
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
                Arguments.of("True", "True", true),
                // Should return false for unequal objects
                Arguments.of("True", "False", false),
                // Should return false for objects of different classes
                Arguments.of("True", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject object = PythonCore.evaluate("True");

        assertEquals(object, object);
    }
}
