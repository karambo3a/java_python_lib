package org.examples;

import org.examples.simple.SimpleExample;
import org.python.integration.core.PythonSession;
import org.python.integration.object.PythonObject;

public class Main {

    private static void runSimpleExamples() {
        try (PythonSession pythonSession = new PythonSession()) {
            System.out.printf("Python version: %s\n", SimpleExample.getPythonVersion());

            var list = SimpleExample.modulesPath().stream().map(path -> ((PythonObject) path).toString()).toList();
            System.out.printf("sys.path = %s\n", list);

            System.out.printf("2^3 = %f\n\n", SimpleExample.pow(2, 3));
        }
    }

    public static void main(String[] args) {
        runSimpleExamples();
    }
}