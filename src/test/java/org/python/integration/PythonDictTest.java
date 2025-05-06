package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonDict;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        assertEquals("2", value.representation());
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
}
