.PHONY: buildNative cleanNative tidy format

CC=g++
CLANG_TIDY=clang-tidy-18
CXXFLAGS=-std=c++20 -fPIC -fno-omit-frame-pointer -g -DPYTHON_VERSION=312
CLANG_FORMAT=clang-format
INCLUDES=-I/usr/include/python3.12 \
            -I/usr/lib/jvm/java-21-openjdk-amd64/include \
            -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux \
			-Isrc/main/cpp/headers
LDFLAGS=-shared -fPIC
OBJS=build/cpp/org_python_integration_core_PythonSession.o \
	 build/cpp/org_python_integration_core_PythonCore.o \
	 build/cpp/org_python_integration_core_PythonScope.o \
	 build/cpp/org_python_integration_object_AbstractPythonObject.o \
	 build/cpp/org_python_integration_object_PythonCallable.o\
	 build/cpp/org_python_integration_object_PythonInt.o\
	 build/cpp/org_python_integration_object_PythonFloat.o\
	 build/cpp/org_python_integration_object_PythonBool.o\
	 build/cpp/org_python_integration_object_PythonStr.o\
	 build/cpp/org_python_integration_object_PythonList.o\
	 build/cpp/org_python_integration_object_PythonDict.o\
	 build/cpp/org_python_integration_object_PythonTuple.o\
	 build/cpp/org_python_integration_object_PythonSet.o\
	 build/cpp/python_object_manager.o \
	 build/cpp/py_java_function.o \
     build/cpp/globals.o

vpath %.cpp src/main/cpp/core src/main/cpp/integration src/main/cpp/object

buildNative: $(OBJS)
	mkdir -p build/libs && \
	$(CC) $(LDFLAGS) $(OBJS) -o build/libs/libnative.so -lpython3.12 -lc

$(OBJS): build/cpp/%.o: %.cpp
	mkdir -p $(@D) && \
	$(CC) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

tidy:
	@for f in src/main/cpp/core/*.cpp src/main/cpp/integration/*.cpp src/main/cpp/object/*.cpp; do\
		echo $$f;\
		$(CLANG_TIDY) -header-filter='src/main/cpp/headers/.*' $$f -- $(CXXFLAGS) $(INCLUDES);\
	done\

format:
	$(CLANG_FORMAT) -i src/main/cpp/core/*.cpp src/main/cpp/integration/*.cpp src/main/cpp/object/*.cpp

cleanNative:
	rm -rf build/cpp build/libs
