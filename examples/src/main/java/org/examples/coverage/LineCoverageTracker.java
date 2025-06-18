package org.examples.coverage;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonScope;
import org.python.integration.object.IPythonObject;
import org.python.integration.object.PythonCallable;
import org.python.integration.object.PythonStr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    public static void writeCoverageToFile() {
        try (BufferedWriter bufferWriter = Files.newBufferedWriter(Paths.get("cov.out"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (var entry : lineCoverage.entrySet()) {
                entry.getValue().forEach((value) -> {
                    try {
                        bufferWriter.write(String.format("%s: %s%n", entry.getKey(), value));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }

    private static IPythonObject tracefunc(IPythonObject frame, IPythonObject event, IPythonObject arg) {
        try (PythonScope pythonScope = new PythonScope()) {
            String coFilename = frame.getAttribute("f_code").getAttribute("co_filename").toString();
            String eventName = event.toString();
            if (sysPath.stream().anyMatch(coFilename::startsWith)) {
                return PythonCore.evaluate("None").keepAlive();
            }

            if (eventName.equals("line")) {
                IPythonObject lineno = frame.getAttribute("f_lineno");
                lineCoverage.computeIfAbsent(coFilename, k -> new ArrayList<>()).add(lineno.toString());
            }
        }
        return tracefuncVar;
    }

    private static void getSysPath(IPythonObject sys, String filePath) {
        try (PythonScope pythonScope = new PythonScope()) {
            var sysPathList = sys.getAttribute("path").asList().get();
            sysPath = sysPathList.stream().map((IPythonObject::toString)).collect(Collectors.toSet());
            sysPath.add("<");
            sysPathList.add(0, PythonStr.from(filePath));
        }
    }

    private static void setTrace() {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            Optional<PythonCallable> settraceOptional = sys.getAttribute("settrace").asCallable();
            settraceOptional.get().call(LineCoverageTracker.tracefuncVar);
        }
    }

    public static void runFilePath(String filePath) {
        try (PythonScope pythonScope = new PythonScope()) {
            IPythonObject sys = PythonCore.importModule("sys");
            Path path = Path.of(filePath);
            String parentPath = path.toAbsolutePath().getParent().toString();
            getSysPath(sys, parentPath);

            setTrace();

            IPythonObject runpy = PythonCore.importModule("runpy");
            Optional<PythonCallable> runPathOptional = runpy.getAttribute("run_path").asCallable();
            PythonStr pythonFilePath = PythonStr.from(path.toAbsolutePath().toString());
            runPathOptional.get().call(pythonFilePath);
        }
    }
}
