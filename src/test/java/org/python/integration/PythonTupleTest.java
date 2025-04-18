package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonTuple;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonTupleTest {

    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
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
    void testListGetSuccessful() {
        PythonTuple tuple = initPythonTuple("(1,2,3)");
        IPythonObject result = tuple.get(1);
        assertNotNull(result);

        Optional<PythonInt> resultOptional = result.asInt();
        assertTrue(resultOptional.isPresent());
        assertEquals(2, resultOptional.get().toJavaInt());
    }

    @Test
    @DisplayName("Should return true if tuple contains item")
    void testListContainsTrueSuccessful() {
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


}
