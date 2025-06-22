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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        IPythonObject integer = PythonInt.from(1);

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(1, pythonInt.get().toJavaLong());
    }

    @Test
    @DisplayName("Should return correct Java int from PythonInt")
    void testToJavaIntSuccessful() {
        IPythonObject integer = PythonInt.from(1);

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(1, pythonInt.get().toJavaInt());
    }

    @Test
    @DisplayName("Should throws when the value overflows an int in toJavaInt")
    void testToJavaIntOverflowThrows() {
        IPythonObject integer = PythonInt.from((long) Integer.MAX_VALUE + 1);

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> pythonInt.get().toJavaInt());
        assertEquals("integer overflow", exception.getMessage());
    }

    @Test
    @DisplayName("Should return correct Java BigInteger from PythonInt")
    void testToJavaBigIntegerSuccessful() {
        BigInteger bigInteger = new BigInteger(Long.valueOf(Long.MAX_VALUE).toString());
        bigInteger = bigInteger.add(bigInteger);
        IPythonObject integer = PythonCore.evaluate(bigInteger.toString());

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(bigInteger, pythonInt.get().toJavaBigInteger());
    }

    @Test
    @DisplayName("Should return PythonInt from Java long")
    void testFromJavaInt() {
        PythonInt pythonInt1 = PythonInt.from(5);
        assertNotNull(pythonInt1);
        assertEquals(5, pythonInt1.toJavaInt());

        PythonInt pythonInt2 = PythonInt.from((long) Integer.MAX_VALUE + 1);
        assertNotNull(pythonInt2);
        assertEquals((long) Integer.MAX_VALUE + 1, pythonInt2.toJavaLong());
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
        PythonInt object = PythonInt.from(1);

        assertEquals(object, object);
    }

    @Test
    @DisplayName("Should successfully return String of a PythonInt")
    void testToString() {
        PythonInt pythonInt = PythonInt.from(1);

        assertEquals("1", pythonInt.toString());
    }

    @Test
    @DisplayName("Should return the same hashCode during a single run")
    void testHashCode() {
        PythonInt pythonInt = PythonInt.from(1);

        int hashCode1 = pythonInt.hashCode();
        int hashCode2 = pythonInt.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should return the same hashCode for equal PythonInt objects")
    void testHashCodeEqualObj() {
        PythonInt pythonInt1 = PythonInt.from(1);
        PythonInt pythonInt2 = PythonInt.from(1);
        assertEquals(pythonInt1, pythonInt2);

        int hashCode1 = pythonInt1.hashCode();
        int hashCode2 = pythonInt2.hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}
