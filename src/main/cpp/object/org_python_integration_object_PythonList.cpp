#include "org_python_integration_object_PythonList.h"
#include "gil.h"
#include "globals.h"
#include "python_object_manager.h"
#include "traits.h"
#include <Python.h>

namespace {
class list {
public:
    list(JNIEnv *env, jobject java_list) : env(env), java_list(java_list) {
        jclass cls = env->FindClass("java/util/List");
        size_method = env->GetMethodID(cls, "size", "()I");
        get_method = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
    }

    std::size_t size() {
        return env->CallIntMethod(java_list, size_method);
    }

    jobject operator[](std::size_t index) {
        return env->CallObjectMethod(java_list, get_method, index);
    }

private:
    JNIEnv *env;
    jobject java_list;
    jmethodID size_method;
    jmethodID get_method;
};

}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonList_of(JNIEnv *env, jclass, jobject java_object) {
    GIL gil;

    PyObject *sequence = object_manager->get_object(env, java_object);
    if (!sequence) {
        return nullptr;
    }
    PyObject *py_list = PySequence_List(sequence);
    if (!py_list) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_list>::convert(env, py_list);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonList_from(JNIEnv *env, jclass, jobject java_list) {
    GIL gil;

    if (!java_list) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java list cannot be null"));
        return nullptr;
    }

    ::list list(env, java_list);
    const std::size_t list_size = list.size();
    PyObject *py_list = PyList_New((Py_ssize_t)list_size);
    if (!py_list) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    for (std::size_t i = 0; i < list_size; ++i) {
        PyObject *py_object = object_manager->get_object(env, list[i]);
        if (!py_object) {
            Py_DecRef(py_list);
            return nullptr;
        }
        Py_IncRef(py_object);
        PyList_SetItem(py_list, (Py_ssize_t)i, py_object);
    }

    return java_traits<python_list>::convert(env, py_list);
}
