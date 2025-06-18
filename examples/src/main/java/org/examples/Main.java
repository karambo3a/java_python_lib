package org.examples;

import org.examples.coverage.LineCoverageTracker;
import org.examples.simple.SimpleExample;
import org.examples.threading.ListMapPythonThreads;
import org.python.integration.core.PythonSession;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonInt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {

    private static void runSimpleExamples() {
        try (PythonSession pythonSession = new PythonSession()) {
            System.out.printf("Python version: %s%n", SimpleExample.getPythonVersion());

            var list = SimpleExample.modulesPath().stream().map(IPythonObject::toString).toList();
            System.out.printf("sys.path = %s%n", list);

            System.out.printf("2^3 = %f%n%n", SimpleExample.pow(2, 3));
        }
    }

    private static void runLineCoverage() {
        try (PythonSession pythonSession = new PythonSession()) {
            String path = "src/main/resources/python_file.py";

            LineCoverageTracker.runFilePath(path);
            LineCoverageTracker.writeCoverageToFile();
        }
    }

    private static void runListMapPythonThreads() {
        try (PythonSession pythonSession = new PythonSession()) {
            List<IPythonObject> list = new ArrayList<>();
            for (int i = 0; i < 26; ++i) {
                list.add(PythonInt.from(i));
            }

            Function<IPythonObject, IPythonObject> function = (value) -> {
                int newValue = value.asInt().get().toJavaInt() * 2;
                return PythonInt.from(newValue);
            };

            System.out.println();
            System.out.println(ListMapPythonThreads.map(list, function));
        }
    }

    public static void main(String[] args) {
        runSimpleExamples();
        runLineCoverage();
        runListMapPythonThreads();
    }
}
