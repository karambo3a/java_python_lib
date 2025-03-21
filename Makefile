all:
	g++ -std=c++17 -fPIC -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include/ -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -c src/main/cpp/org_example_Main.cpp -o src/main/cpp/org_example_Main.o
	g++ -shared -fPIC src/main/cpp/org_example_Main.o -o src/main/cpp/libnative.so -lpython3.12 -lc
	java -cp src/main/java -Djava.library.path="src/main/cpp" org.example.Main

clean:
	rm -f src/main/cpp/org_example_Main.o src/main/cpp/libnative.so
