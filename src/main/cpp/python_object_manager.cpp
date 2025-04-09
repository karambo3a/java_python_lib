#include "headers/python_object_manager.h"
#include "headers/python_object_factory.h"
#include <string>
#include <iostream>


PythonObjectManager::~PythonObjectManager() {
    for (auto py_object: this->py_objects) {
        Py_XDECREF(py_object);
    }
}


std::size_t PythonObjectManager::add_object(PyObject* py_object, bool is_borrowed) {
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


PyObject* PythonObjectManager::get_object(JNIEnv *env, std::size_t index) {
    PyObject* py_object = this->py_objects[index];
    if (!py_object) {
        jthrowable java_exception = create_native_operation_exception(env, "Associated Python object with Java object is NULL");
        env->Throw(java_exception);
        return nullptr;
    }
    return py_object;
}


PyObject* PythonObjectManager::get_object(JNIEnv *env, jobject java_object) {
    std::size_t index = this->get_index(env, java_object);
    PyObject* py_object = this->get_object(env, index);
    return py_object;
}


void PythonObjectManager::free_object(JNIEnv *env, jobject java_object) {
    std::size_t index = this->get_index(env, java_object);
    if (!this->py_objects[index]) {
        jthrowable java_exception = create_native_operation_exception(env, ("Double object free on index=" + std::to_string(index)).c_str());
        env->Throw(java_exception);
    }
    Py_DecRef(this->py_objects[index]);
    this->py_objects[index] = nullptr;
}


std::size_t PythonObjectManager::get_index(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "index", "J");
    jlong index = env->GetLongField(java_object, field);
    return (std::size_t)index;
}
