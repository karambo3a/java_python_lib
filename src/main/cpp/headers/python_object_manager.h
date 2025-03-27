#ifndef PYTHON_OBJECT_MANAGER_H
#define PYTHON_OBJECT_MANAGER_H

#include <Python.h>
#include <vector>

class PythonObjectManager {
public:
    PythonObjectManager();

    ~PythonObjectManager();

    PythonObjectManager(const PythonObjectManager&) = delete;

    PythonObjectManager& operator=(const PythonObjectManager&) = delete;

    PythonObjectManager(PythonObjectManager&& other) = delete;

    PythonObjectManager& operator=(PythonObjectManager&& other) = delete;

    std::size_t add_object(PyObject* py_object);

    PyObject* get_object(std::size_t index);

    void free_object(std::size_t index);

private:
    std::vector<PyObject*> py_objects;
};

#endif // PYTHON_OBJECT_H
