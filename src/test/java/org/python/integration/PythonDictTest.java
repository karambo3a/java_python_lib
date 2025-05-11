package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonDict;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonDictTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
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
        var entryList = entries.stream().map((pyObject) -> Map.entry(pyObject.getKey().representation(), pyObject.getValue().representation()));
        assertThat(entryList)
                .hasSize(2)
                .containsExactlyInAnyOrder(Map.entry("2", "3"), Map.entry("1", "2"));

        for (var entry : entries) {
            entry.setValue(PythonCore.evaluate("1"));
        }
        assertEquals("{1: 1, 2: 1}", pythonDict.representation());
    }

    @Test
    @DisplayName("Should successfully modify the dict via entrySet")
    public void testEntrySetSetValueSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();
        for (var entry : entries) {
            entry.setValue(PythonCore.evaluate("1"));
        }
        assertEquals("{1: 1, 2: 1}", pythonDict.representation());
    }

    @Test
    @DisplayName("Should successfully remove entry of the dict via entrySet ")
    public void testEntrySetRemoveSuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        Set<Map.Entry<IPythonObject, IPythonObject>> entries = pythonDict.entrySet();
        assertTrue(entries.remove(new AbstractMap.SimpleEntry<>(PythonCore.evaluate("1"), PythonCore.evaluate("2"))));
        assertEquals("{2: 3}", pythonDict.representation());

        assertFalse(entries.remove(new AbstractMap.SimpleEntry<>(PythonCore.evaluate("2"), PythonCore.evaluate("2"))));
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
    public void testGetExistentKey() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject value = pythonDict.get(PythonCore.evaluate("1"));
        assertEquals(2, value.asInt().get().toJavaInt());
    }

    @Test
    @DisplayName("Should successfully return dict item")
    public void testGetNonExistentKey() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertNull(pythonDict.get(PythonCore.evaluate("3")));
    }

    @Test
    @DisplayName("Should successfully set dict item and return old value (existent key)")
    public void testPutExistentKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.put(PythonCore.evaluate("1"), PythonCore.evaluate("5"));
        assertNotNull(prevValue);
        assertEquals("2", prevValue.representation());
        assertEquals("{1: 5, 2: 3}", pythonDict.representation());
    }

    @Test
    @DisplayName("Should successfully set dict item and return null (non-existent key)")
    public void testPutNonExistentKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.put(PythonCore.evaluate("3"), PythonCore.evaluate("4"));
        assertNull(prevValue);
    }

    @Test
    @DisplayName("Should return true (key contains in the dict)")
    public void testContainsKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertTrue(pythonDict.containsKey(PythonCore.evaluate("1")));
    }

    @Test
    @DisplayName("Should return false (key doesn't contain in the dict)")
    public void testDoNotContainKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        assertFalse(pythonDict.containsKey(PythonCore.evaluate("3")));
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
        IPythonObject object = PythonCore.evaluate("{1:2, 2:3}");

        assertEquals(object, object);
    }

    @Test
    @DisplayName("Should return prev value (dict contains the key)")
    public void testRemoveContainsKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.remove(PythonCore.evaluate("1"));
        assertNotNull(prevValue);
        assertEquals("2", prevValue.representation());
        assertEquals("{2: 3}", pythonDict.representation());
    }

    @Test
    @DisplayName("Should return prev value (dict contains the key)")
    public void testRemoveDoNotContainKeySuccessful() {
        PythonDict pythonDict = initPythonDict("{1:2, 2:3}");

        IPythonObject prevValue = pythonDict.remove(PythonCore.evaluate("3"));
        assertNull(prevValue);
        assertEquals("{1: 2, 2: 3}", pythonDict.representation());
    }
}
