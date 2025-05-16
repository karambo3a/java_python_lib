#include "headers/org_python_integration_object_PythonList.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include "headers/python_object_manager.h"
#include <Python.h>

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonList_of(JNIEnv *env, jclass, jobject java_object) {
    PyObject *sequence = object_manager->get_object(env, java_object);
    if (!sequence) {
        return nullptr;
    }
    PyObject *list = PySequence_List(sequence);
    if (!list) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }
    const std::size_t index = object_manager->add_object(list);
    jobject python_list = create_python_list(env, index, object_manager->get_scope_id());
    return python_list;
}

namespace {
    std::size_t list_size(JNIEnv *env, jobject java_list) {
        jclass cls = env->FindClass("java/util/List");
        jmethodID size_method = env->GetMethodID(cls, "size", "()I");
        return env->CallIntMethod(java_list, size_method);
    }

    jobject list_get(JNIEnv *env, jobject java_list, std::size_t index) {
        jclass cls = env->FindClass("java/util/List");
        jmethodID get_method = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
        return env->CallObjectMethod(java_list, get_method, index);
    }
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonList_from(JNIEnv *env, jclass, jobject java_list) {
    if (!java_list) {
        jthrowable java_exception = create_native_operation_exception(env, "Java list cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    const std::size_t size = list_size(env, java_list);
    PyObject *py_list = PyList_New((Py_ssize_t)size);
    if (!py_list) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    for (std::size_t i = 0; i < size; ++i) {
        jobject java_object = list_get(env, java_list, i);
        PyObject *py_object = object_manager->get_object(env, java_object);
        if (!py_object) {
            Py_DecRef(py_list);
            return nullptr;
        }
        Py_IncRef(py_object);
        PyList_SetItem(py_list, (Py_ssize_t)i, py_object);
    }
    const std::size_t index = object_manager->add_object(py_list);
    return create_python_list(env, index, object_manager->get_scope_id());
}
