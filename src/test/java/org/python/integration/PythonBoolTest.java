package org.python.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonBoolTest {

    private PythonSession pythonSession = new PythonSession();


    @Test
    @DisplayName("Should return Java boolean from PythonBool")
    void testToJavaBooleanSuccessful() {
        IPythonObject trueBool = PythonCore.evaluate("True");
        IPythonObject falseBool = PythonCore.evaluate("False");

        assertTrue(trueBool.asBool().get().toJavaBoolean());
        assertFalse(falseBool.asBool().get().toJavaBoolean());
    }

}
