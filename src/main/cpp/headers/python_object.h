#ifndef PYTHON_OBJECT_H
#define PYTHON_OBJECT_H

#include <Python.h>

class PythonObject {
public:
    PythonObject(PyObject* PyObject);

    ~PythonObject();

    PythonObject(const PythonObject&) = delete;

    PythonObject& operator=(const PythonObject&) = delete;

    PythonObject(PythonObject&& other) = delete;

    PythonObject& operator=(PythonObject&& other) = delete;

    PyObject* get_py_object();

    void set_py_object(PyObject *pyObj);

private:
    PyObject* pyObj;
};

#endif // PYTHON_OBJECT_H
