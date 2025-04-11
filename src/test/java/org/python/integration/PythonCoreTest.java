package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PythonCoreTest {

    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
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
}
