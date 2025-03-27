#include "headers/python_object.h"


PythonObject::PythonObject(PyObject *pyObj): pyObj(pyObj) {
    if (this->pyObj) {
        Py_INCREF(this->pyObj);
    }
}

PythonObject::~PythonObject() {
    if (this->pyObj){
        Py_DECREF(this->pyObj);
    }
}

PyObject* PythonObject::get_py_object() {
    return this->pyObj;
}

void PythonObject::set_py_object(PyObject *pyObj) {
    if (this->pyObj) {
        this->pyObj = pyObj;
    } else {
        this->pyObj = pyObj;
        Py_IncRef(this->pyObj);
    }
}
