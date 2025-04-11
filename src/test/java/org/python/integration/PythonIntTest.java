package org.python.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonIntTest {
    private PythonSession pythonSession = new PythonSession();

    @Test
    @DisplayName("Should return correct Java int from PythonInt")
    void testToJavaIntSuccessful() {
        IPythonObject integer = PythonCore.evaluate("1");

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(1, pythonInt.get().toJavaInt());
    }

}
