package org.examples.coverage;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonStr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class LineCoverageTracker {
    private static final PythonCallable tracefuncVar = PythonCallable.from(LineCoverageTracker::tracefunc);
    private static Set<String> sysPath = new HashSet<>();
    private static final Map<String, List<String>> lineCoverage = new HashMap<>();

    public static Map<String, List<String>> getLineCoverage() {
        return lineCoverage;
    }

    private static void createOrCleanFile() {
        File covFile = new File("cov.out");
        try (FileOutputStream fileOutputStream = new FileOutputStream("cov.out")) {
            fileOutputStream.write("".getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeCoverageToFile() {
        createOrCleanFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream("cov.out", true)) {
            for (var entry : lineCoverage.entrySet()) {
                entry.getValue().stream().forEach((value) -> {
                    try {
                        fileOutputStream.write(String.format("%s: %s\n", entry.getKey(), value).getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static IPythonObject tracefunc(IPythonObject frame, IPythonObject event, IPythonObject arg) {
        try (PythonScope pythonScope = new PythonScope()) {
            String coFilename = frame.getAttribute("f_code").getAttribute("co_filename").asStr().get().toJavaString();
            if (event.asStr().get().equals(PythonStr.from("call")) && sysPath.stream().anyMatch(coFilename::contains)) {
                return PythonCore.evaluate("None").keepAlive();
            }

            if (sysPath.stream().anyMatch(coFilename::contains)) {
                return PythonCore.evaluate("None").keepAlive();
            }

            if (event.asStr().get().equals(PythonStr.from("line"))) {
                IPythonObject lineno = frame.getAttribute("f_lineno");
                lineCoverage.computeIfAbsent(coFilename, k -> new ArrayList<>()).add(lineno.toString());
            }
        }
        return tracefuncVar;
    }

    private static void getSysPath(IPythonObject sys, String filePath) {
        try (PythonScope pythonScope = new PythonScope()) {
            var sysPathList = sys.getAttribute("path").asList().get();
            sysPath = sysPathList.stream().map((iPythonObject -> iPythonObject.asStr().get().toJavaString())).collect(Collectors.toSet());
            sysPath.add("<");
            sysPathList.add(0, PythonStr.from(filePath));
        }
    }

    private static void setTrace() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            Optional<PythonCallable> settraceOptional;
            settraceOptional = sys.getAttribute("settrace").asCallable();
            settraceOptional.get().call(LineCoverageTracker.tracefuncVar);
        }
    }

    public static void runFilePath(String filePath) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            Path path = Path.of(filePath);
            String parentPath = path.getParent().toAbsolutePath().toString();
            getSysPath(sys, parentPath);

            setTrace();

            IPythonObject runpy = PythonCore.importModule("runpy");
            Optional<PythonCallable> runPathOptional = runpy.getAttribute("run_path").asCallable();
            PythonStr pythonFilePath = PythonStr.from(path.toAbsolutePath().toString());
            runPathOptional.get().call(pythonFilePath);

        }
    }
}











