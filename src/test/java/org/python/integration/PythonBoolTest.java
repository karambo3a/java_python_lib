package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonBool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonBoolTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
    }


    @Test
    @DisplayName("Should return Java boolean from PythonBool")
    void testToJavaBooleanSuccessful() {
        IPythonObject trueBool = PythonCore.evaluate("True");
        IPythonObject falseBool = PythonCore.evaluate("False");

        assertTrue(trueBool.asBool().get().toJavaBoolean());
        assertFalse(falseBool.asBool().get().toJavaBoolean());
    }


    @Test
    @DisplayName("Should return Java boolean from PythonBool")
    void testFromJavaBooleanSuccessful() {
        PythonBool pythonBool = PythonBool.from(true);

        assertNotNull(pythonBool);
        assertTrue(pythonBool.toJavaBoolean());
    }
}
