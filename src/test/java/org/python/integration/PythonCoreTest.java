package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;

import java.util.Map;
import java.util.Set;

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
    @DisplayName("Should throw PythonException when evaluating invalid Python syntax")
    void testEvaluateThrows() {
        assertThrows(PythonException.class, () -> PythonCore.evaluate("[1, 2, 3"));
    }


    @Test
    @DisplayName("Should free PythonObject")
    @Disabled("TODO: implement test for free()")
    void shouldCleanUpResourcesWhenFreed() {
        // TODO: implement test for free()
    }


    @Test
    @DisplayName("Should import module by name")
    void shouldSuccessfullyImportModule() {
        IPythonObject module = PythonCore.importModule("math");
        assertNotNull(module);
        assertTrue(module.toString().contains("math"));
    }

    @Test
    @DisplayName("Should import submodules by names")
    void shouldSuccessfullyImportSubmodulesFromModule() {
        Map<String, IPythonObject> submodules = PythonCore.fromImport("math", "tan", "sin", "cos");
        assertNotNull(submodules);
        for (var entry : submodules.entrySet()) {
            assertNotNull(entry.getKey());
            assertTrue(Set.of("tan", "sin", "cos").contains(entry.getKey()));
            assertNotNull(entry.getValue());
            assertTrue(Set.of("tan", "sin", "cos").contains(entry.getValue().toString().substring(19, 22)));
        }
    }
}
