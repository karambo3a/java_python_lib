package org.examples.threading;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonInt;
import org.python.integration.object.PythonList;

import java.util.List;
import java.util.function.Function;

public class ListMapThreads {

    static public PythonList map(List<IPythonObject> list, Function<IPythonObject, IPythonObject> function) {
        try (PythonScope pythonScope = new PythonScope()) {
            PythonList pythonList = PythonList.from(list);
            PythonCallable pythonCallable = PythonCallable.from(function);
            IPythonObject threadPoolExecutor = getThreadPoolExecutor();
            PythonCallable map = threadPoolExecutor.getAttribute("map").asCallable().get();

            IPythonObject res = map.call(pythonCallable, pythonList);
            return PythonList.of(res).keepAlive();
        }
    }


    static private IPythonObject getThreadPoolExecutor() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject threadPoolExecutorClass = PythonCore.fromImport("concurrent.futures.thread", "ThreadPoolExecutor").get("ThreadPoolExecutor");
            PythonCallable newAttr = threadPoolExecutorClass.getAttribute("__new__").asCallable().get();
            IPythonObject threadPoolExecutor = newAttr.call(threadPoolExecutorClass);

            PythonCallable initAttr = threadPoolExecutor.getAttribute("__init__").asCallable().get();
            PythonInt maxWorkers = PythonInt.from(5);
            initAttr.call(maxWorkers);
            return threadPoolExecutor.keepAlive();
        }
    }
}
