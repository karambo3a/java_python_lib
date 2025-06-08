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
import org.python.integration.object.PythonDict;
import org.python.integration.object.PythonInt;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonDictTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    private PythonDict initPythonDict(String representation) {
        IPythonObject pythonObject = PythonCore.evaluate(representation);
        Optional<PythonDict> listOptional = pythonObject.asDict();
        return listOptional.orElse(null);
    }


    @Test
    @DisplayName("Should successfully return entrySet of the dict")
    public void testEntrySetSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();
        assertNotNull(entries);
        assertEquals(2, entries.size());
        var entryList = entries.stream().map((pyObject) -> Map.entry(pyObject.getKey().toString(), pyObject.getValue().toString()));
        assertThat(entryList)
                .hasSize(2)
                .containsExactlyInAnyOrder(Map.entry("1", "2"), Map.entry("2", "3"));

        for (var entry : entries) {
            entry.setValue(PythonCore.evaluate("1"));
        }
        assertEquals(PythonDict.from(Map.of(PythonInt.from(1), PythonInt.from(1), PythonInt.from(2), PythonInt.from(1))), pythonDict);

        assertTrue(entries.remove(Map.entry(PythonInt.from(1), PythonInt.from(1))));
        assertEquals(PythonDict.from(Map.of(PythonInt.from(2), PythonInt.from(1))), pythonDict);
    }

    @Test
    @DisplayName("Should successfully update dict via entrySet")
    public void testUpdateDictEntrySetSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");
        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();

        for (var entry : entries) {
            entry.setValue(PythonCore.evaluate("1"));
        }
        assertEquals(PythonDict.from(Map.of(PythonInt.from(1), PythonInt.from(1), PythonInt.from(2), PythonInt.from(1))), pythonDict);
    }

    @Test
    @DisplayName("Should successfully remove from the dict via entrySet")
    public void testRemoveFromDictViaEntrySetSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");
        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();

        // Successful remove
        assertTrue(entries.remove(Map.entry(PythonInt.from(1), PythonInt.from(2))));
        assertEquals(PythonDict.from(Map.of(PythonInt.from(2), PythonInt.from(3))), pythonDict);

        // Remove of non-existent entry
        assertFalse(entries.remove(Map.entry(PythonInt.from(1), PythonInt.from(1))));

        // Remove of non-entry
        assertFalse(entries.remove(PythonInt.from(1)));

        // Remove of entry with non-IPythonObject key and value
        assertFalse(entries.remove(Map.entry(1, 1)));
    }

    @Test
    @DisplayName("Should successfully call entrySet contains")
    public void testEntrySetContainsSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");
        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();

        assertTrue(entries.contains(Map.entry(PythonInt.from(1), PythonInt.from(2))));
        assertFalse(entries.contains(Map.entry(PythonInt.from(1), PythonInt.from(1))));
    }

    @Test
    @DisplayName("Should successfully return size of the non-empty dict")
    public void testSizeSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertEquals(2, pythonDict.size());
    }

    @Test
    @DisplayName("Should successfully return size of the empty dict")
    public void testSizeEmptyDictSuccessful() {
        PythonDict pythonDict = initPythonDict("{}");

        assertEquals(0, pythonDict.size());
    }

    @Test
    @DisplayName("Should successfully return dict item")
    public void testGetExistentKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject value = pythonDict.get(PythonInt.from(1));
        assertEquals(2, value.asInt().get().toJavaInt());
    }

    @Test
    @DisplayName("Should return null when get non-existent item from dict")
    public void testGetNonExistentKeyThrows() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertNull(pythonDict.get(PythonInt.from(3)));
    }

    @Test
    @DisplayName("Should return null when get non-existent item from dict")
    public void testGetNonIPythonObjectKeyThrows() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertNull(pythonDict.get(3));
    }

    @Test
    @DisplayName("Should successfully set dict item and return old value (existent key)")
    public void testPutExistentKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.put(PythonInt.from(1), PythonInt.from(5));
        assertNotNull(prevValue);
        assertEquals(PythonInt.from(2), prevValue);
        assertEquals("{1: 5, 2: 3}", pythonDict.toString());
    }

    @Test
    @DisplayName("Should successfully set dict item and return null (non-existent key)")
    public void testPutNonExistentKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.put(PythonInt.from(3), PythonInt.from(4));
        assertNull(prevValue);
    }

    @Test
    @DisplayName("Should return true (key contains in the dict)")
    public void testContainsKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertTrue(pythonDict.containsKey(PythonInt.from(1)));
    }

    @Test
    @DisplayName("Should return false (key doesn't contain in the dict)")
    public void testDoNotContainKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertFalse(pythonDict.containsKey(PythonInt.from(3)));
    }

    @Test
    @DisplayName("Should return false (key is not instance of IPythonObject)")
    public void testKeyIsNotIPythonObjectSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertFalse(pythonDict.containsKey(3));
    }

    @Test
    @DisplayName("Should return prev value (dict contains the key)")
    public void testRemoveContainsKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.remove(PythonInt.from(1));
        assertNotNull(prevValue);
        assertEquals(PythonInt.from(2), prevValue);
        assertEquals("{2: 3}", pythonDict.toString());
    }

    @Test
    @DisplayName("Should return null (dict does not contain the key)")
    public void testRemoveDoNotContainKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.remove(PythonInt.from(3));
        assertNull(prevValue);
        assertEquals("{1: 2, 2: 3}", pythonDict.toString());
    }

    @Test
    @DisplayName("Should successfully convert Map to PythonDict")
    void testFromSuccessful() {
        PythonDict dict = PythonDict.from(Map.of(PythonInt.from(1), PythonInt.from(2)));
        assertNotNull(dict);

        assertEquals("{1: 2}", dict.toString());
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
                Arguments.of("{1:2, 2:3}", "{1:2, 2:3}", true),
                // Should return false for unequal objects
                Arguments.of("{1:2}", "{2:3}", false),
                // Should return false for objects of different classes
                Arguments.of("{1:2, 2:3}", "[1,2,3]", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject object = initPythonDict("{1:2, 2:3}");

        assertEquals(object, object);
    }

    @Test
    @DisplayName("Should successfully return String of a PythonDict")
    void testToString() {
        PythonDict pythonDict1 = PythonDict.from(Map.of(PythonInt.from(1), PythonInt.from(2)));

        assertEquals("{1: 2}", pythonDict1.toString());
    }

    @Test
    @DisplayName("Should throws when getting hashcode of unhashable dict")
    void testHashCodeThrows() {
        PythonDict pythonDict = PythonDict.from(Map.of(PythonInt.from(1), PythonInt.from(2)));

        PythonException exception = assertThrows(PythonException.class, pythonDict::hashCode);
        assertEquals("unhashable type: 'dict'", exception.getValue().toString());
        exception.free();
    }
}
