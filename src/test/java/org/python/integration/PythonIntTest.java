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
import org.python.integration.object.PythonInt;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonIntTest {
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
    @DisplayName("Should return correct Java long from PythonInt")
    void testToJavaLongSuccessful() {
        IPythonObject integer = PythonCore.evaluate("1");

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(1, pythonInt.get().toJavaLong());
    }

    @Test
    @DisplayName("Should return correct Java BigInteger from PythonInt")
    void testToJavaBigIntegerWithBigIntegerSuccessful() {
        BigInteger bigInteger = new BigInteger(Long.valueOf(Long.MAX_VALUE).toString());
        bigInteger = bigInteger.add(bigInteger);
        IPythonObject integer = PythonCore.evaluate(bigInteger.toString());

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(bigInteger, pythonInt.get().toJavaBigInteger());
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
                Arguments.of("1", "1", true),
                // Should return false for unequal objects
                Arguments.of("1", "2", false),
                // Should return false for objects of different classes
                Arguments.of("1", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject object = PythonCore.evaluate("1");

        assertEquals(object, object);
    }

    @Test
    @DisplayName("Should return new PythonInt from Java long")
    void testFromJavaInt() {
        PythonInt pythonInt1 = PythonInt.from(5);
        assertNotNull(pythonInt1);
        assertEquals(5, pythonInt1.toJavaLong());

        PythonInt pythonInt2 = PythonInt.from(1000);
        assertNotNull(pythonInt2);
        assertEquals(1000, pythonInt2.toJavaLong());
    }
}
