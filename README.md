# Java-Python Integration Library

---

This library provides tools for Java-Python integration, enabling:

* **Python interpreter control**
    * Initialize and finalize Python interpreter from Java
    * Handle exceptions occurring from 
      * The Python interpreter 
      * Native (JNI) code
* **Python objects management**
    * Automatic reference counting for Python objects
    * Flexible Python objects release from Java (both automatic and manual)
* **Convert between Java and Python objects**
    * Primitive types (`int`, `float`, `bool`, `str`)
    * Collections (`list`, `tuple`, `dict`, `set`)
    * Custom types
* **Callable Python object**
    * Calling Python functions and methods from Java.
    * Invoke Java functional interfaces from Python interpreter

## Installation and usage
1. **Edit Makefile**

```Makefile
  # Change to your Python version (39 for Python 3.9 etc.)
  CXXFLAGS= ... -DPYTHON_VERSION=312
  
  # Path to your Python and JVM
  INCLUDES= ... -I/usr/include/python3.12 -I/usr/lib/jvm/java-21-openjdk-amd64/include -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux 
```
Also change `-lpython3.12` to your Python version.
2. **Build**

Build library using

```
  # Builds both Java and native components
  ./gradlew build
```
Built files location: `build/libs/`
3. **Usage**

Add `java_python_lib-1.0-SNAPSHOT.jar` to classpath and make sure that `libnative.so` is in your `java.library.path`.

## Dependencies
// TODO (write all dependencies)