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
import org.python.integration.object.PythonSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonSetTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    private PythonSet initPythonSet(String representation) {
        Optional<PythonSet> setOptional = PythonCore.evaluate(representation).asSet();
        return setOptional.orElse(null);
    }

    @Test
    @DisplayName("Should return set iterator")
    void testIteratorSuccessful() {
        PythonSet set = initPythonSet("{1,2,3}");
        var expected = List.of("1", "2", "3");
        int index = 0;
        for (IPythonObject next : set) {
            assertNotNull(next);
            assertEquals(expected.get(index), next.toString());
            index++;
        }
    }

    @Test
    @DisplayName("Should successfully return set size")
    void testSizeSuccessful() {
        PythonSet set = initPythonSet("{1,2,3}");
        assertEquals(3, set.size());
    }

    @Test
    @DisplayName("Should successfully return empty set size")
    void testSizeEmptySuccessful() {
        PythonSet set = initPythonSet("set()");
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("Should successfully convert Set to PythonSet")
    void testFromSuccessful() {
        IPythonObject first = PythonInt.from(1);
        IPythonObject second = PythonInt.from(2);
        PythonSet set = PythonSet.from(Set.of(first, second));
        assertNotNull(set);

        var expected = Set.of(first, second);
        for (var i : set) {
            assertTrue(expected.contains(i));
        }
    }

    @ParameterizedTest
    @MethodSource("provideInputForEqualsTest")
    public void testEquals(String value1, String value2, boolean expected) {
        PythonSet object1 = initPythonSet(value1);
        IPythonObject object2 = PythonCore.evaluate(value2);

        assertEquals(expected, object1.equals(object2));
    }

    private static Stream<Arguments> provideInputForEqualsTest() {
        return Stream.of(
                // Should return true for equal objects
                Arguments.of("{1,2,3}", "{1,2,3}", true),
                // Should return false for unequal objects
                Arguments.of("{1}", "{2}", false),
                // Should return false for objects of different classes
                Arguments.of("{1,2,3}", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject obj = PythonCore.evaluate("{1,2,3}");

        assertEquals(obj, obj);
    }

    @Test
    @DisplayName("Should successfully return String of a PythonSet")
    void testToString() {
        PythonSet pythonSet = initPythonSet("{1,2,3}");

        assertEquals("{1, 2, 3}", pythonSet.toString());
    }

    @Test
    @DisplayName("Should throws when getting hashcode of unhashable set")
    void testHashCode() {
        PythonSet pythonSet = PythonSet.from(Set.of(PythonInt.from(1)));

        PythonException exception = assertThrows(PythonException.class, pythonSet::hashCode);
        assertEquals("unhashable type: 'set'", exception.getValue().toString());
        exception.free();
    }
}
