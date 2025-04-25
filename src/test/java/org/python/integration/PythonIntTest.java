package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonIntTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
    }

    @Test
    @DisplayName("Should return correct Java int from PythonInt")
    void testToJavaIntSuccessful() {
        IPythonObject integer = PythonCore.evaluate("1");

        Optional<PythonInt> pythonInt = integer.asInt();
        assertTrue(pythonInt.isPresent());
        assertEquals(1, pythonInt.get().toJavaInt());
    }


    @Test
    @DisplayName("Should return new PythonInt from Java int")
    void testFromJavaInt() {
        PythonInt pythonInt1 = PythonInt.from(5);
        assertNotNull(pythonInt1);
        assertEquals(5, pythonInt1.toJavaInt());

        PythonInt pythonInt2 = PythonInt.from(1000);
        assertNotNull(pythonInt2);
        assertEquals(1000, pythonInt2.toJavaInt());
    }
}
