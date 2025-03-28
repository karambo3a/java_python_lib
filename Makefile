.PHONY: buildNative cleanNative

buildNative:
	mkdir -p build/cpp build/libs && \
	g++ -std=c++20 -fPIC -fno-omit-frame-pointer -g -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include/ -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -c src/main/cpp/org_example_Main.cpp -o build/cpp/org_example_Main.o && \
	g++ -std=c++20 -fPIC -fno-omit-frame-pointer -g -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include/ -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -c src/main/cpp/org_example_PythonInitializer.cpp -o build/cpp/org_example_PythonInitializer.o && \
	g++ -std=c++20 -fPIC -fno-omit-frame-pointer -g -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include/ -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -c src/main/cpp/python_object_manager.cpp -o build/cpp/python_object_manager.o && \
	g++ -std=c++20 -fPIC -fno-omit-frame-pointer -g -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include/ -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -c src/main/cpp/globals.cpp -o build/cpp/globals.o && \
	g++ -shared -fPIC build/cpp/org_example_Main.o build/cpp/org_example_PythonInitializer.o build/cpp/python_object_manager.o build/cpp/globals.o -o build/libs/libnative.so -lpython3.12 -lc

cleanNative:
	rm -rf build/cpp build/libs
