package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PythonStrTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
    }

    @Test
    @DisplayName("Should return Java String from PythonStr")
    void testToJavaStringSuccessful() {
        IPythonObject pythonStr = PythonCore.evaluate("\"string!\"");

        assertNotNull(pythonStr);
        assertEquals("string!", pythonStr.asStr().get().toJavaString());
    }
}
