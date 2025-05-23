#include "../headers/python_object_manager.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"
#include <cstddef>
#include <jni.h>
#include <string>

PythonObjectManager::PythonObjectManager(PythonObjectManager *prev_object_manager, std::size_t scope_id)
    : prev_object_manager(prev_object_manager), scope_id(scope_id) {
}

PythonObjectManager::~PythonObjectManager() {
    for (auto py_object : this->py_objects) {
        Py_XDECREF(py_object);
    }
}

PythonObjectManager *PythonObjectManager::get_prev_object_manager() {
    return this->prev_object_manager;
}

std::size_t PythonObjectManager::get_scope_id() {
    return this->scope_id;
}

std::size_t PythonObjectManager::add_object(PyObject *py_object, bool is_borrowed) {
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

PyObject *PythonObjectManager::get_object(std::size_t index) {
    return this->py_objects[index];
}

PyObject *PythonObjectManager::get_object(JNIEnv *env, std::size_t index, std::size_t scope_id) {
    PythonObjectManager *object_manager = this;
    while (object_manager->get_prev_object_manager() != nullptr && object_manager->get_scope_id() != scope_id) {
        object_manager = object_manager->get_prev_object_manager();
    }

    if (object_manager->get_scope_id() != scope_id) {
        jthrowable java_exception =
            create_native_operation_exception(env, "Scope associated with Python object is closed");
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject *py_object = object_manager->get_object(index);
    if (!py_object) {
        jthrowable java_exception =
            create_native_operation_exception(env, "Associated Python object with Java object is NULL");
        env->Throw(java_exception);
        return nullptr;
    }
    return py_object;
}

PyObject *PythonObjectManager::get_object(JNIEnv *env, jobject java_object) {
    const std::size_t index = get_index(env, java_object);
    const std::size_t scope_id = get_scope(env, java_object);
    PyObject *py_object = this->get_object(env, index, scope_id);
    return py_object;
}

void PythonObjectManager::free_object(JNIEnv *env, jobject java_object) {
    const std::size_t index = get_index(env, java_object);
    if (!this->py_objects[index]) {
        jthrowable java_exception =
            create_native_operation_exception(env, ("Double object free on index=" + std::to_string(index)).c_str());
        env->Throw(java_exception);
        return;
    }
    this->py_objects[index] = nullptr;
    Py_XDECREF(this->py_objects[index]);
}
