package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonBool;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonDict;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonObjectTest {

    private PythonSession pythonSession;
    private IPythonObject list;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
        list = PythonCore.evaluate("[1,2,3]");
    }


    @Test
    @DisplayName("Should return correct string representation")
    void testRepresentationSuccessful() {
        assertEquals("[1, 2, 3]", list.representation());
    }


    @Test
    @DisplayName("Should successful get existing attribute from Python object")
    void testGetAttributeSuccessful() {
        IPythonObject len = list.getAttribute("__len__");

        assertNotNull(len);
        assertTrue(len.representation().contains("__len__"));
    }


    @Test
    @DisplayName("Should throws PythonException when getting non-existent attribute")
    void testGetAttributeNonExistentAttributeThrows() {
        PythonException pythonException = assertThrows(PythonException.class, () -> list.getAttribute("not existent attribute"));
        assertNotNull(pythonException.getValue());
    }

    @Test
    @DisplayName("Should throws NativeOperationException when getting attribute with null name")
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
    @DisplayName("Should successfully convert PythonObject to PythonList")
    void testAsListSuccessful() {
        Optional<PythonList> pythonList = list.asList();
        assertTrue(pythonList.isPresent());
        assertNotNull(pythonList.get());
        assertEquals("[1, 2, 3]", pythonList.get().representation());
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
        assertEquals("{1: 1, 2: 2}", pythonDict.get().representation());
    }

    @Test
    @DisplayName("Should return empty Optional when converting not dict PythonObject to PythonDict")
    void testAsDictUnsuccessful() {
        Optional<PythonDict> pythonDict = list.asDict();
        assertTrue(pythonDict.isEmpty());
    }
}
