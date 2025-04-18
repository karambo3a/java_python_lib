package org.python.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonSet;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PythonSetTest {
    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        pythonSession = new PythonSession();
    }

    private PythonSet initPythonSet(String representation) {
        IPythonObject pythonObject = PythonCore.evaluate(representation);
        Optional<PythonSet> setOptional = pythonObject.asSet();
        return setOptional.orElse(null);
    }


    @Test
    @DisplayName("Should return set iterator")
    void testListSizeSuccessful() {
        PythonSet set = initPythonSet("{1,2,3}");
        var expected = List.of("1", "2", "3");
        int index = 0;
        for (IPythonObject next : set) {
            assertNotNull(next);
            assertEquals(expected.get(index), next.representation());
            index++;
        }
    }
}
