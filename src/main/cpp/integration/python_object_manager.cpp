#include "python_object_manager.h"
#include "globals.h"
#include "traits.h"
#include <cstddef>
#include <jni.h>
#include <string>

PythonObjectManager *PythonObjectManager::find_object_manager_by_scope(JNIEnv *env, std::size_t scope_id) {
    PythonObjectManager *curr_object_manager = object_manager;
    while (curr_object_manager->get_prev_object_manager() != nullptr && curr_object_manager->get_scope_id() != scope_id) {
        curr_object_manager = curr_object_manager->get_prev_object_manager();
    }

    if (curr_object_manager->get_scope_id() != scope_id) {
        const char *message = "Scope associated with Python object is closed";
        env->Throw(java_traits<native_operation_exception>::create(env, message));
        return nullptr;
    }
    return curr_object_manager;
}

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
    PythonObjectManager *object_manager = find_object_manager_by_scope(env, scope_id);

    PyObject *py_object = object_manager->get_object(index);
    if (!py_object) {
        env->Throw(
            java_traits<native_operation_exception>::create(env, "Associated Python object with Java object is NULL")
        );
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
    PythonObjectManager *object_manager = find_object_manager_by_scope(env, get_scope(env, java_object));

    const std::size_t index = get_index(env, java_object);
    if (!object_manager->py_objects[index]) {
        const std::string message = "Double object free on index=" + std::to_string(index);
        env->Throw(java_traits<native_operation_exception>::create(env, message.c_str()));
        return;
    }
    object_manager->py_objects[index] = nullptr;
    Py_XDECREF(object_manager->py_objects[index]);
}
