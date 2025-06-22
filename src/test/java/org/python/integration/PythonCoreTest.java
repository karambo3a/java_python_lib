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
import org.python.integration.object.PythonInt;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonCoreTest {

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
    @DisplayName("Should successfully evaluate valid Python expression")
    void testEvaluateSuccessful() {
        IPythonObject list = PythonCore.evaluate("[1, 2, 3]");

        assertNotNull(list);
    }

    @Test
    @DisplayName("Should throw when evaluating invalid Python syntax")
    void testEvaluateThrows() {
        PythonException exception = assertThrows(PythonException.class, () -> PythonCore.evaluate("[1, 2, 3"));
        exception.free();
    }

    @Test
    @DisplayName("Should successfully free PythonObject")
    void testCleanUpResourcesWhenFreedPythonObjectSuccessful() {
        PythonInt pythonInt = PythonInt.from(1);
        assertEquals(1, pythonInt.toJavaInt());

        PythonCore.free(pythonInt);
        NativeOperationException exception = assertThrows(NativeOperationException.class, pythonInt::toJavaInt);
        assertEquals("Associated Python object with Java object is NULL", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully free PythonObject from another scope")
    void testCleanUpResourcesWhenFreedPythonObjectFromAnotherScopeSuccessful() {
        PythonInt pythonInt = PythonInt.from(1);
        assertEquals(1, pythonInt.toJavaInt());

        try (PythonScope pythonScope = new PythonScope()) {
            PythonCore.free(pythonInt);
            NativeOperationException exception = assertThrows(NativeOperationException.class, pythonInt::toJavaInt);
            assertEquals("Associated Python object with Java object is NULL", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should throws when a freed PythonObject is released")
    void testCleanUpResourcesWhenFreedPythonObjectIsReleasedThrows() {
        PythonInt pythonInt = PythonInt.from(1);
        assertEquals(1, pythonInt.toJavaInt());

        PythonCore.free(pythonInt);
        NativeOperationException exception = assertThrows(NativeOperationException.class, () -> PythonCore.free(pythonInt));
        assertTrue(exception.getMessage().contains("Double object free on index="));
    }

    @Test
    @DisplayName("Should import module by name")
    void testImportModuleSuccessfully() {
        IPythonObject module = PythonCore.importModule("math");
        assertNotNull(module);
        assertTrue(module.toString().contains("math"));
    }

    @Test
    @DisplayName("Should import submodules by names")
    void testImportSubmodulesFromModuleSuccessfully() {
        Set<String> expectedFunctions = Set.of("tan", "sin", "cos");
        Map<String, IPythonObject> submodules = PythonCore.fromImport("math", "tan", "sin", "cos");
        assertNotNull(submodules);
        assertEquals(expectedFunctions.size(), submodules.size());

        for (var entry : submodules.entrySet()) {
            String functionName = entry.getKey();
            IPythonObject function = entry.getValue();

            assertNotNull(functionName);
            assertTrue(Set.of("tan", "sin", "cos").contains(functionName));

            assertNotNull(function);
            assertTrue(function.toString().contains(functionName));
        }
    }
}
