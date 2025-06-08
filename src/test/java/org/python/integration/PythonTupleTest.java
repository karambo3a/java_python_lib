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
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonTuple;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonTupleTest {

    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    private PythonTuple initPythonTuple(String representation) {
        IPythonObject pythonObject = PythonCore.evaluate(representation);
        Optional<PythonTuple> tupleOptional = pythonObject.asTuple();
        return tupleOptional.orElse(null);
    }

    @Test
    @DisplayName("Should return correct tuple size")
    void testTupleSizeSuccessful() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        assertEquals(3, tuple.size());
    }

    @Test
    @DisplayName("Should return correct empty tuple size")
    void testTupleSizeEmptySuccessful() {
        PythonTuple tuple = initPythonTuple("()");
        assertEquals(0, tuple.size());
    }

    @Test
    @DisplayName("Should return correct tuple item")
    void testTupleGetSuccessful() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        IPythonObject result = tuple.get(1);
        assertNotNull(result);

        assertEquals(2, result.asInt().get().toJavaLong());
    }

    @Test
    @DisplayName("Should throws when getting tuple item with out of range index")
    void testTupleGetThrows() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        PythonException exception = assertThrows(PythonException.class, () -> tuple.get(3));

        assertEquals("tuple index out of range", exception.getValue().toString());
        exception.free();
    }

    @Test
    @DisplayName("Should return true when tuple contains item")
    void testTupleContainsTrueSuccessful() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        IPythonObject item = PythonCore.evaluate("1");

        assertTrue(tuple.contains(item));
    }

    @Test
    @DisplayName("Should return false if tuple does not contain item")
    void testListContainsFalseSuccessful() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        IPythonObject item = PythonCore.evaluate("4");

        assertFalse(tuple.contains(item));
    }

    @Test
    @DisplayName("Should successfully convert List to PythonTuple")
    void testFromSuccessful() {
        IPythonObject first = PythonInt.from(1);
        IPythonObject second = PythonInt.from(2);
        var t = List.of(first, second);
        PythonTuple tuple = PythonTuple.from(t);
        assertNotNull(tuple);

        assertEquals(first.representation(), tuple.getFirst().representation());
        assertEquals(second.representation(), tuple.getLast().representation());
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
                Arguments.of("(1,2,3)", "(1,2,3)", true),
                // Should return false for unequal objects
                Arguments.of("(1)", "(2)", false),
                // Should return false for objects of different classes
                Arguments.of("(1,2,3)", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject obj = PythonCore.evaluate("(1,2,3)");

        assertEquals(obj, obj);
    }

    @Test
    @DisplayName("Should successfully return String of a PythonTuple")
    void testToString() {
        PythonTuple pythonTuple = PythonTuple.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2)
        ));

        assertEquals("(1, 2)", pythonTuple.toString());
    }

    @Test
    @DisplayName("Should return the same hashCode during a single run")
    void testHashCode() {
        PythonTuple pythonTuple = PythonTuple.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2)
        ));

        int hashCode1 = pythonTuple.hashCode();
        int hashCode2 = pythonTuple.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should return the same hashCode for equal PythonTuple objects")
    void testHashCodeEqualObj() {
        PythonTuple pythonTuple1 = PythonTuple.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2)
        ));
        PythonTuple pythonTuple2 = PythonTuple.from(List.of(
                PythonInt.from(1),
                PythonInt.from(2)
        ));
        assertEquals(pythonTuple1, pythonTuple2);

        int hashCode1 = pythonTuple1.hashCode();
        int hashCode2 = pythonTuple2.hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}
