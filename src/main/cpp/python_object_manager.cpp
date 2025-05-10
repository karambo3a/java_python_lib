#include "headers/python_object_manager.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <string>

PythonObjectManager::PythonObjectManager(PythonObjectManager *prev_object_manager, std::size_t scope): prev_object_manager(prev_object_manager), scope(scope) {}


PythonObjectManager::~PythonObjectManager() {
    for (auto py_object: this->py_objects) {
        Py_XDECREF(py_object);
    }
}


PythonObjectManager* PythonObjectManager::get_prev_object_manager() {
    return this->prev_object_manager;
}


std::size_t PythonObjectManager::get_object_manager_scope() {
    return this->scope;
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


PyObject* PythonObjectManager::get_object(std::size_t index) {
    return this->py_objects[index];
}


PyObject* PythonObjectManager::get_object(JNIEnv *env, std::size_t index, std::size_t scope) {
    PythonObjectManager* object_manager = this;
    while (object_manager->get_prev_object_manager() != nullptr && object_manager->get_object_manager_scope() != scope) {
        object_manager = object_manager->get_prev_object_manager();
    }

    PyObject* py_object = object_manager->get_object(index);
    if (!py_object) {
        jthrowable java_exception = create_native_operation_exception(env, "Associated Python object with Java object is NULL");
        env->Throw(java_exception);
        return nullptr;
    }
    return py_object;
}


PyObject* PythonObjectManager::get_object(JNIEnv *env, jobject java_object) {
    std::size_t index = this->get_index(env, java_object);
    std::size_t scope = this->get_scope(env, java_object);
    PyObject* py_object = this->get_object(env, index, scope);
    return py_object;
}


void PythonObjectManager::free_object(JNIEnv *env, jobject java_object) {
    std::size_t index = this->get_index(env, java_object);
    if (!this->py_objects[index]) {
        jthrowable java_exception = create_native_operation_exception(env, ("Double object free on index=" + std::to_string(index)).c_str());
        env->Throw(java_exception);
        return;
    }
    this->py_objects[index] = nullptr;
    Py_XDECREF(this->py_objects[index]);
}


std::size_t PythonObjectManager::get_scope(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "scope", "J");
    jlong scope = env->GetLongField(java_object, field);
    return (std::size_t)scope;
}


std::size_t PythonObjectManager::get_index(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "index", "J");
    jlong index = env->GetLongField(java_object, field);
    return (std::size_t)index;
}


void initialize_scope() {
    object_manager = new PythonObjectManager(object_manager, object_manager->get_object_manager_scope() + 1);
}


void finalize_scope() {
    PythonObjectManager *curr_object_manager = object_manager;
    object_manager = curr_object_manager->get_prev_object_manager();
    delete curr_object_manager;
    curr_object_manager = nullptr;
}
