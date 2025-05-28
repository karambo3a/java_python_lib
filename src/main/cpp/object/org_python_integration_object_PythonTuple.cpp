#include "org_python_integration_object_PythonTuple.h"
#include "globals.h"
#include "python_object_manager.h"
#include "traits.h"
#include <Python.h>

namespace {
class tuple {
public:
    tuple(JNIEnv *env, jobject java_tuple) : env(env), java_tuple(java_tuple) {
    }

    std::size_t size() {
        jclass cls = env->FindClass("java/util/List");
        jmethodID size_method = env->GetMethodID(cls, "size", "()I");
        return env->CallIntMethod(java_tuple, size_method);
    }

    jobject operator[](std::size_t index) {
        jclass cls = env->FindClass("java/util/List");
        jmethodID get_method = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
        return env->CallObjectMethod(java_tuple, get_method, index);
    }

private:
    JNIEnv *env;
    jobject java_tuple;
};

}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonTuple_from(JNIEnv *env, jclass, jobject java_tuple) {
    if (!java_tuple) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java tuple cannot be null"));
        return nullptr;
    }

    ::tuple tuple(env, java_tuple);
    const std::size_t tuple_size = tuple.size();
    PyObject *py_tuple = PyTuple_New((Py_ssize_t)tuple_size);
    if (!py_tuple) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    for (std::size_t i = 0; i < tuple_size; ++i) {
        PyObject *py_object = object_manager->get_object(env, tuple[i]);
        if (!py_object) {
            return nullptr;
        }
        Py_IncRef(py_object);
        PyTuple_SetItem(py_tuple, (Py_ssize_t)i, py_object);
    }

    return java_traits<python_tuple>::convert(env, py_tuple);
}
