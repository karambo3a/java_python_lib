package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonScope;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PythonScopeTest {

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
    @DisplayName("Should successfully close the scope")
    void testCloseSuccessfully() {
        PythonScope pythonScope1 = new PythonScope();
        PythonScope pythonScope2 = new PythonScope();
        assertDoesNotThrow(pythonScope2::close);
        assertDoesNotThrow(pythonScope1::close);
    }

    @Test
    @DisplayName("Throws an exception when not the last scope is closed")
    void testCloseThrows() {
        PythonScope pythonScope1 = new PythonScope();
        PythonScope pythonScope2 = new PythonScope();
        NativeOperationException exception = assertThrows(NativeOperationException.class, pythonScope1::close);
        assertEquals("Cannot close non-last scope", exception.getMessage());

        pythonScope2.close();
        pythonScope1.close();
    }
}
