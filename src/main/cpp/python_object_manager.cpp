#include "headers/python_object_manager.h"
#include <iostream>

PythonObjectManager::~PythonObjectManager() {
    for (auto py_object: this->py_objects) {
        Py_XDECREF(py_object);
    }
}

std::size_t PythonObjectManager::add_object(PyObject* py_object, bool is_borrowed) {
    if (!py_object) {
        return -1;
    }
    if (is_borrowed) {
        Py_IncRef(py_object);
    }
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

PyObject* PythonObjectManager::get_object(JNIEnv *env, jobject java_object) {
    std::size_t index = this->get_index(env, java_object);
    return this->get_object(index);
}


void PythonObjectManager::free_object(std::size_t index) {
    Py_XDECREF(this->py_objects[index]);
    this->py_objects[index] = nullptr;
}


std::size_t PythonObjectManager::get_index(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "index", "J");
    jlong index = env->GetLongField(java_object, field);
    return (std::size_t)index;
}
