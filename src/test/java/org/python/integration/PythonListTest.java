package org.python.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonListTest {

    private PythonSession pythonSession;

    @BeforeEach
    void initPythonSession() {
        this.pythonSession = new PythonSession();
    }

    @AfterEach
    void closePythonSession() {
        this.pythonSession.close();
    }

    private PythonList initPythonList(String representation) {
        IPythonObject pythonObject = PythonCore.evaluate(representation);
        Optional<PythonList> listOptional = pythonObject.asList();
        return listOptional.orElse(null);
    }


    @Test
    @DisplayName("Should return correct list size")
    void testListSizeSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        assertEquals(3, list.size());
    }


    @Test
    @DisplayName("Should return correct list item")
    void testListGetSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject result = list.get(1);
        assertNotNull(result);

        Optional<PythonInt> resultOptional = result.asInt();
        assertNotNull(resultOptional);
        assertTrue(resultOptional.isPresent());
        assertEquals(2, resultOptional.get().toJavaLong());
    }


    @Test
    @DisplayName("Should successfully set list item")
    void testListSetSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonCore.evaluate("1");
        IPythonObject result = list.set(2, item);

        assertNotNull(result);
        Optional<PythonInt> resultOptional = result.asInt();
        assertTrue(resultOptional.isPresent());
        assertEquals(3, resultOptional.get().toJavaLong());

        assertEquals("[1, 2, 1]", list.representation());
    }

    @Test
    @DisplayName("Should successfully add item to list")
    void testListAddSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonCore.evaluate("1");
        list.add(3, item);

        assertEquals("[1, 2, 3, 1]", list.representation());
    }

    @Test
    @DisplayName("Should return true if list contains item")
    void testListContainsTrueSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonCore.evaluate("1");

        assertTrue(list.contains(item));
    }

    @Test
    @DisplayName("Should return false if tuple does not contain item")
    void testListContainsFalseSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonCore.evaluate("4");

        assertFalse(list.contains(item));
    }

    @ParameterizedTest
    @MethodSource("provideInputForEqualsTest")
    public void testEquals(String value1, String value2, boolean expected) {
        IPythonObject object1 = PythonCore.evaluate(value1);
        IPythonObject object2 = PythonCore.evaluate(value2);

        assertEquals(expected, object1.equals(object2));
    }

    private static Stream<Arguments> provideInputForEqualsTest() {
        return Stream.of(
                // Should return true for equal objects
                Arguments.of("[1,2,3]", "[1,2,3]", true),
                // Should return false for unequal objects
                Arguments.of("[1]", "[2]", false),
                // Should return false for objects of different classes
                Arguments.of("[1]", "1", false)
        );
    }

    @Test
    @DisplayName("Equals should return true Java boolean (equals with the same object)")
    void testEqualsWithTheSameObj() {
        IPythonObject object = PythonCore.evaluate("[1,2,3]");

        assertEquals(object, object);
    }


    @Test
    @DisplayName("Should successfully convert List to PythonList")
    void testFromSuccessful() {
        IPythonObject first = PythonInt.from(1);
        IPythonObject second = PythonInt.from(2);
        var l = List.of(first, second);
        PythonList list = PythonList.from(l);
        assertNotNull(list);

        assertEquals(first.representation(), list.getFirst().representation());
        assertEquals(second.representation(), list.getLast().representation());
    }
}
