package org.examples.threading;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.List;
import java.util.function.Function;

public class ListMapPythonThreads {

    public static PythonList map(List<IPythonObject> list, Function<IPythonObject, IPythonObject> function) {
        try (PythonScope pythonScope = new PythonScope()) {
            PythonList pythonList = PythonList.from(list);
            PythonCallable pythonCallable = PythonCallable.from(function);
            IPythonObject threadPoolExecutor = getThreadPoolExecutor();
            PythonCallable map = threadPoolExecutor.getAttribute("map").asCallable().get();

            IPythonObject res = map.call(pythonCallable, pythonList);
            return PythonList.of(res).keepAlive();
        }
    }


    private static IPythonObject getThreadPoolExecutor() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject threadPoolExecutorClass = PythonCore.fromImportOne("concurrent.futures.thread", "ThreadPoolExecutor");
            PythonInt maxWorkers = PythonInt.from(5);
            IPythonObject threadPoolExecutor = threadPoolExecutorClass.asCallable().get().call(maxWorkers);
            return threadPoolExecutor.keepAlive();
        }
    }
}
