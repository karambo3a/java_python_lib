#ifndef PYTHON_OBJECT_MANAGER_H
#define PYTHON_OBJECT_MANAGER_H

#include <Python.h>
#include <vector>
#include <jni.h>

class PythonObjectManager {
public:
    PythonObjectManager() = default;

    ~PythonObjectManager();

    PythonObjectManager(const PythonObjectManager&) = delete;

    PythonObjectManager& operator=(const PythonObjectManager&) = delete;

    PythonObjectManager(PythonObjectManager&& other) = delete;

    PythonObjectManager& operator=(PythonObjectManager&& other) = delete;

    std::size_t add_object(PyObject* py_object, bool is_borrowed = false);

    PyObject* get_object(std::size_t index);

    PyObject* get_object(JNIEnv *env, jobject java_object);

    void free_object(std::size_t index);

    std::size_t get_index(JNIEnv *env, jobject java_object);

private:
    std::vector<PyObject*> py_objects;
};

#endif // PYTHON_OBJECT_H
