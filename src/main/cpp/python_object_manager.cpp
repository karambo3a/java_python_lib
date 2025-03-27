#include "headers/python_object_manager.h"


PythonObjectManager::PythonObjectManager() {}

PythonObjectManager::~PythonObjectManager() {
    for (auto py_object: this->py_objects) {
        if (py_object) {
            Py_DecRef(py_object);
        }
    }
}

std::size_t PythonObjectManager::add_object(PyObject* py_object) {
    if (!py_object) {
        return -1;
    }
    Py_IncRef(py_object);
    for (std::size_t i = 0; i < this->py_objects.size(); ++i) {
        if (!this->py_objects[i]) {
            py_objects[i] = py_object;
            return i;
        }
    }
    this->py_objects.push_back(py_object);
    return this->py_objects.size() - 1;
}

PyObject* PythonObjectManager::get_object(std::size_t index) {
    return this->py_objects[index];
}


void PythonObjectManager::free_object(std::size_t index) {
    Py_DecRef(this->py_objects[index]);
    this->py_objects[index] = nullptr;
}
