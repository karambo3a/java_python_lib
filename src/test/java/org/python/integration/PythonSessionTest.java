package org.python.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PythonSessionTest {

    @Test
    @DisplayName("Should throw when creating new session if there is already an open session")
    void testCreateSessionWithOpenedSessionThrows() {
        PythonSession pythonSession = new PythonSession();

        NativeOperationException exception = assertThrows(NativeOperationException.class, PythonSession::new);
        assertEquals("There is already an open session", exception.getMessage());

        pythonSession.close();
    }
}
