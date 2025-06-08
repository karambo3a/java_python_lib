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
import org.python.integration.object.PythonStr;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PythonStrTest {
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
    @DisplayName("Should return Java String from PythonStr")
    void testToJavaStringSuccessful() {
        PythonStr pythonStr = PythonStr.from("string!");

        assertNotNull(pythonStr);
        assertEquals("string!", pythonStr.toJavaString());
    }

    @Test
    @DisplayName("Should return new PythonStr from Java String")
    void testFromJavaInt() {
        PythonStr pythonStr1 = PythonStr.from("string!");
        assertNotNull(pythonStr1);
        assertEquals("string!", pythonStr1.toJavaString());
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
                Arguments.of("\"str\"", "\"str\"", true),
                // Should return false for unequal objects
                Arguments.of("\"str1\"", "\"str2\"", false),
                // Should return false for objects of different classes
                Arguments.of("\"str\"", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject obj = PythonCore.evaluate("\"str\"");

        assertEquals(obj, obj);
    }

    @Test
    @DisplayName("Should return the same hashCode during a single run")
    void testHashCode() {
        PythonStr pythonStr = PythonStr.from("str");

        int hashCode1 = pythonStr.hashCode();
        int hashCode2 = pythonStr.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should return the same hashCode for equal PythonStr objects")
    void testHashCodeEqualObj() {
        PythonStr pythonStr1 = PythonStr.from("str");
        PythonStr pythonStr2 = PythonStr.from("str");
        assertEquals(pythonStr1, pythonStr2);

        int hashCode1 = pythonStr1.hashCode();
        int hashCode2 = pythonStr2.hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}