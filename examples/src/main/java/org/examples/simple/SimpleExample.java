package org.examples.simple;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.Map;
import java.util.Optional;

public class SimpleExample {

    static public String getPythonVersion() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            var version = sys.getAttribute("version");
            return version.toString();
        }
    }


    static public PythonList modulesPath() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            Optional<PythonList> listOptional = sys.getAttribute("path").asList();
            return listOptional.map(PythonList::keepAlive).orElse(null);
        }
    }

    static public double pow(int num, int pow) {
        try (PythonScope pythonScope = new PythonScope()) {
            PythonInt pythonNum = PythonInt.from(num);
            PythonInt pythonPow = PythonInt.from(pow);
            Map<String, IPythonObject> mathModule = PythonCore.fromImport("math", "pow");
            Optional<PythonCallable> powOptional = mathModule.get("pow").asCallable();
            return powOptional.map(pythonCallable -> Double.parseDouble(pythonCallable.call(pythonNum, pythonPow).toString())).orElse(-1.0);
        }
    }
}
