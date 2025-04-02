.PHONY: buildNative cleanNative

CC=g++
CXXFLAGS=-std=c++20 -fPIC -fno-omit-frame-pointer -g
INCLUDES=-I/usr/include/python3.12 \
            -I/usr/lib/jvm/java-21-openjdk-amd64/include \
            -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux
LDFLAGS=-shared -fPIC
OBJS=build/cpp/org_python_integration_PythonSession.o \
	 build/cpp/org_python_integration_PythonCore.o \
	 build/cpp/org_python_integration_AbstractPythonObject.o \
	 build/cpp/org_python_integration_PythonCallable.o\
	 build/cpp/org_python_integration_PythonInt.o\
	 build/cpp/python_object_manager.o \
	 build/cpp/python_object_factory.o \
     build/cpp/globals.o

buildNative: $(OBJS)
	mkdir -p build/libs && \
	$(CC) $(LDFLAGS) $(OBJS) -o build/libs/libnative.so -lpython3.12 -lc

$(OBJS): build/cpp/%.o: src/main/cpp/%.cpp
	mkdir -p build/cpp/ && \
	$(CC) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

cleanNative:
	rm -rf build/cpp build/libs
