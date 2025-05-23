#include "../headers/org_python_integration_object_PythonTuple.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"
#include "../headers/python_object_manager.h"
#include <Python.h>

namespace {
std::size_t tuple_size(JNIEnv *env, jobject java_tuple) {
    jclass cls = env->FindClass("java/util/List");
    jmethodID size_method = env->GetMethodID(cls, "size", "()I");
    return env->CallIntMethod(java_tuple, size_method);
}

jobject tuple_get(JNIEnv *env, jobject java_tuple, std::size_t index) {
    jclass cls = env->FindClass("java/util/List");
    jmethodID get_method = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
    return env->CallObjectMethod(java_tuple, get_method, index);
}
}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonTuple_from(JNIEnv *env, jclass, jobject java_tuple) {
    if (!java_tuple) {
        jthrowable java_exception = create_native_operation_exception(env, "Java tuple cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    const std::size_t size = tuple_size(env, java_tuple);
    PyObject *py_tuple = PyTuple_New((Py_ssize_t)size);
    if (!py_tuple) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    for (std::size_t i = 0; i < size; ++i) {
        jobject java_object = tuple_get(env, java_tuple, i);
        PyObject *py_object = object_manager->get_object(env, java_object);
        if (!py_object) {
            return nullptr;
        }
        Py_IncRef(py_object);
        PyTuple_SetItem(py_tuple, (Py_ssize_t)i, py_object);
    }

    const std::size_t index = object_manager->add_object(py_tuple);
    return create_python_tuple(env, index, object_manager->get_scope_id());
}
