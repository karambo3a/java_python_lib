#ifndef PYTHON_OBJECT_MANAGER_H
#define PYTHON_OBJECT_MANAGER_H

#include <Python.h>
#include <jni.h>
#include <vector>

class PythonObjectManager {
public:
    PythonObjectManager() = default;

    PythonObjectManager(PythonObjectManager *prev_object_manager, std::size_t scope_id);

    ~PythonObjectManager();

    PythonObjectManager(const PythonObjectManager &) = delete;

    PythonObjectManager &operator=(const PythonObjectManager &) = delete;

    PythonObjectManager(PythonObjectManager &&) = delete;

    PythonObjectManager &operator=(PythonObjectManager &&) = delete;

    std::size_t add_object(PyObject *py_object, bool is_borrowed = false);

    PythonObjectManager *get_prev_object_manager();

    std::size_t get_scope_id();

    PyObject *get_object(JNIEnv *env, std::size_t index, std::size_t scope_id);

    PyObject *get_object(JNIEnv *env, jobject java_object);

    PyObject *get_object(std::size_t index);

    static void free_object(JNIEnv *env, jobject java_object);

private:
    std::vector<PyObject *> py_objects;
    PythonObjectManager *prev_object_manager = nullptr;
    std::size_t scope_id = 0;

    static PythonObjectManager *find_object_manager_by_scope(JNIEnv *env, std::size_t scope_id);
};

#endif  // PYTHON_OBJECT_MANAGER_H
