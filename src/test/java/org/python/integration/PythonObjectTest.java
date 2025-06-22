package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonBool;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonDict;
import org.python.integration.object.PythonFloat;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;
import org.python.integration.object.PythonSet;
import org.python.integration.object.PythonStr;
import org.python.integration.object.PythonTuple;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonObjectTest {

    private PythonSession pythonSession;
    private IPythonObject list;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
        list = PythonCore.evaluate("[1,2,3]");
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    private IPythonObject createPythonObject() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject pythonObject = PythonInt.from(1);
            return pythonObject.keepAlive();
        }
    }

    @Test
    @DisplayName("Successfully call keepAlive")
    void testKeepAliveSuccessful() {
        IPythonObject pythonObject = assertDoesNotThrow(this::createPythonObject);
        assertNotNull(pythonObject);
        assertEquals(1, pythonObject.asInt().get().toJavaInt());
    }

    @Test
    @DisplayName("Throws exception when calling keepAlive in root scope")
    void testKeepAliveThrows() {
        NativeOperationException exception = assertThrows(NativeOperationException.class, list::keepAlive);
        assertNotNull(exception);
        assertEquals("Cannot move object to higher scope: already in root scope", exception.getMessage());
    }

    @Test
    @DisplayName("Throws an exception  when calling keepAlive fro released python object")
    void testKeepAliveForReleasedObjectThrows() {
        PythonInt pythonInt = PythonInt.from(1);
        PythonCore.free(pythonInt);

        NativeOperationException exception = assertThrows(NativeOperationException.class, pythonInt::keepAlive);
        assertNotNull(exception);
        assertEquals("Associated Python object with Java object is NULL", exception.getMessage());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testRepresentationSuccessful() {
        assertEquals("[1, 2, 3]", list.representation());
    }

    @Test
    @DisplayName("Should successful get existing attribute from PythonObject")
    void testGetAttributeSuccessful() {
        IPythonObject len = list.getAttribute("__len__");

        assertNotNull(len);
        assertTrue(len.toString().contains("__len__"));
    }

    @Test
    @DisplayName("Should throws when getting non-existent attribute")
    void testGetAttributeNonExistentAttributeThrows() {
        PythonException exception = assertThrows(PythonException.class, () -> list.getAttribute("not existent attribute"));
        assertNotNull(exception.getValue());
        assertTrue(exception.getValue().toString().contains("has no attribute"));
        exception.free();
    }

    @Test
    @DisplayName("Should throws when getting attribute with null name")
    void testGetAttributeNUllAttributeNameThrows() {
        NativeOperationException nativeOperationException = assertThrows(NativeOperationException.class, () -> list.getAttribute(null));
        assertEquals("Attribute name cannot be null", nativeOperationException.getMessage());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonCallable")
    void testAsCallableSuccessful() {
        IPythonObject len = list.getAttribute("__len__");

        Optional<PythonCallable> pythonCallable = len.asCallable();
        assertTrue(pythonCallable.isPresent());
        assertNotNull(pythonCallable.get());
    }

    @Test
    @DisplayName("Should return empty Optional when converting non-callable PythonObject to PythonCallable")
    void testAsCallableUnsuccessful() {
        Optional<PythonCallable> pythonCallable = list.asCallable();
        assertTrue(pythonCallable.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonInt")
    void testAsIntSuccessful() {
        IPythonObject integer = PythonCore.evaluate("1");

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertNotNull(pythonInt.get());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not int PythonObject to PythonInt")
    void testAsIntUnsuccessful() {
        Optional<PythonInt> pythonInt = list.asInt();
        assertTrue(pythonInt.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonFloat")
    void testAsFloatSuccessful() {
        IPythonObject integer = PythonCore.evaluate("0.1");

        Optional<PythonFloat> pythonFloat = integer.asFloat();
        assertTrue(pythonFloat.isPresent());
        assertNotNull(pythonFloat.get());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not float PythonObject to PythonFloat")
    void testAsFloatUnsuccessful() {
        Optional<PythonFloat> pythonFloat = list.asFloat();
        assertTrue(pythonFloat.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonBool")
    void testAsBoolSuccessful() {
        IPythonObject bool = PythonCore.evaluate("True");
        Optional<PythonBool> pythonBool = bool.asBool();
        assertTrue(pythonBool.isPresent());
        assertNotNull(pythonBool.get());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not bool PythonObject to PythonBool")
    void testAsBoolUnsuccessful() {
        Optional<PythonBool> pythonBool = list.asBool();
        assertTrue(pythonBool.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonStr")
    void testAsStrSuccessful() {
        IPythonObject str = PythonCore.evaluate("\"str\"");
        Optional<PythonStr> pythonStr = str.asStr();
        assertTrue(pythonStr.isPresent());
        assertEquals("str", pythonStr.get().toString());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not str PythonObject to PythonStr")
    void testAsStrUnsuccessful() {
        Optional<PythonStr> pythonStr = list.asStr();
        assertTrue(pythonStr.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonList")
    void testAsListSuccessful() {
        Optional<PythonList> pythonList = list.asList();
        assertTrue(pythonList.isPresent());
        assertNotNull(pythonList.get());
        assertEquals("[1, 2, 3]", pythonList.get().toString());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not list PythonObject to PythonList")
    void testAsListUnsuccessful() {
        Optional<PythonList> pythonList = PythonCore.evaluate("1").asList();
        assertTrue(pythonList.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonDict")
    void testAsDictSuccessful() {
        IPythonObject dict = PythonCore.evaluate("{1: 1, 2: 2}");
        Optional<PythonDict> pythonDict = dict.asDict();
        assertTrue(pythonDict.isPresent());
        assertNotNull(pythonDict.get());
        assertEquals("{1: 1, 2: 2}", pythonDict.get().toString());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not dict PythonObject to PythonDict")
    void testAsDictUnsuccessful() {
        Optional<PythonDict> pythonDict = list.asDict();
        assertTrue(pythonDict.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonTuple")
    void testAsTupleSuccessful() {
        Optional<PythonTuple> pythonTuple = PythonCore.evaluate("(1,2,3)").asTuple();
        assertTrue(pythonTuple.isPresent());
        assertNotNull(pythonTuple.get());
        assertEquals("(1, 2, 3)", pythonTuple.get().toString());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not tuple PythonObject to PythonTuple")
    void testAsTupleUnsuccessful() {
        Optional<PythonTuple> pythonTuple = PythonCore.evaluate("1").asTuple();
        assertTrue(pythonTuple.isEmpty());
    }

    @Test
    @DisplayName("Should successfully convert PythonObject to PythonSet")
    void testAsSetSuccessful() {
        Optional<PythonSet> pythonSet = PythonCore.evaluate("{1,2,3}").asSet();
        assertTrue(pythonSet.isPresent());
        assertNotNull(pythonSet.get());
        assertEquals("{1, 2, 3}", pythonSet.get().toString());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not set PythonObject to PythonSet")
    void testAsSetUnsuccessful() {
        Optional<PythonSet> pythonSet = PythonCore.evaluate("1").asSet();
        assertTrue(pythonSet.isEmpty());
    }
}
