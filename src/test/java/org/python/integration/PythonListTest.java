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
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("Should return 0 for empty list")
    void testEmptyListSizeSuccessful() {
        PythonList list = initPythonList("[]");
        assertEquals(0, list.size());
    }


    @Test
    @DisplayName("Should return correct list item")
    void testListGetSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject result = list.get(1);
        assertNotNull(result);

        assertEquals(2, result.asInt().get().toJavaInt());
    }

    @Test
    @DisplayName("Should throws when getting list item with out of range index")
    void testListGetOutOfRangeThrows() {
        PythonList list = initPythonList("[1,2,3]");
        PythonException exception = assertThrows(PythonException.class, () -> list.get(3));

        assertEquals("list index out of range", exception.getValue().toString());
        exception.free();
    }

    @Test
    @DisplayName("Should return correct list item with negative index")
    void testListGetNegativeThrows() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject result = list.get(-1);

        assertEquals(3, result.asInt().get().toJavaInt());
    }

    @Test
    @DisplayName("Should successfully set list item")
    void testListSetSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonInt.from(1);
        IPythonObject result = list.set(2, item);

        assertEquals(3, result.asInt().get().toJavaLong());

        assertEquals("[1, 2, 1]", list.toString());
    }

    @Test
    @DisplayName("Should throws when setting list item with out of range index")
    void testListSetOutOfRangeThrows() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonInt.from(1);
        PythonException exception = assertThrows(PythonException.class, () -> list.set(3, item));

        assertEquals("list index out of range", exception.getValue().toString());
        exception.free();
    }

    @Test
    @DisplayName("Should successfully set list item with negative index")
    void testListSetNegativeSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonInt.from(10);
        IPythonObject prev = list.set(-3, item);

        assertEquals(PythonInt.from(1), prev.asInt().get());
        assertEquals("[10, 2, 3]", list.toString());
    }

    @Test
    @DisplayName("Should successfully add item to list")
    void testListAddSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonInt.from(1);
        list.add(5, item);

        assertEquals("[1, 2, 3, 1]", list.toString());
    }

    @Test
    @DisplayName("Should successfully add to list item with negative index")
    void testListAddNegativeSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonInt.from(1);
        list.add(-5, item);

        assertEquals("[1, 1, 2, 3]", list.toString());
    }

    @Test
    @DisplayName("Should return true if list contains item")
    void testListContainsTrueSuccessful() {
        PythonList list = initPythonList("[1,2,3]");
        IPythonObject item = PythonCore.evaluate("1");

        assertTrue(list.contains(item));
    }

    @Test
    @DisplayName("Should return false if list does not contain item")
    void testListContainsFalseSuccessful() {
        PythonList list = initPythonList("[1,2,3]");

        IPythonObject item = PythonCore.evaluate("4");
        assertFalse(list.contains(item));

        assertFalse(list.contains(1));
    }

    @Test
    @DisplayName("Should successfully convert List to PythonList")
    void testFromSuccessful() {
        IPythonObject first = PythonInt.from(1);
        IPythonObject second = PythonInt.from(2);
        PythonList list = PythonList.from(List.of(first, second));
        assertNotNull(list);

        assertEquals(PythonCore.evaluate("[1, 2]"), list);
    }

    @Test
    @DisplayName("Should successfully return list from sequence IPythonObject")
    void testOfSuccessful() {
        IPythonObject sequence = PythonCore.evaluate("(i for i in range(3))");
        assertEquals("[0, 1, 2]", PythonList.of(sequence).toString());
    }

    @Test
    @DisplayName("Should throws when creating list from non-sequence IPythonObject")
    void testOfThrows() {
        IPythonObject pythonInt = PythonInt.from(1);
        PythonException exception = assertThrows(PythonException.class, () -> PythonList.of(pythonInt));
        assertTrue(exception.getValue().toString().contains("is not iterable"));
        exception.free();
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
    @DisplayName("Should successfully return String of a PythonList")
    void testToString() {
        PythonList pythonList = PythonList.from(List.of(PythonInt.from(1)));

        assertEquals("[1]", pythonList.toString());
    }

    @Test
    @DisplayName("Should throws when getting hashcode of unhashable list")
    void testHashCode() {
        PythonList pythonList = PythonList.from(List.of(PythonInt.from(1)));

        PythonException exception = assertThrows(PythonException.class, pythonList::hashCode);
        assertEquals("unhashable type: 'list'", exception.getValue().toString());
        exception.free();
    }
}
