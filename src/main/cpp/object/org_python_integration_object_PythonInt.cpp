#include "org_python_integration_object_PythonInt.h"
#include "globals.h"
#include "traits.h"

JNIEXPORT jint JNICALL Java_org_python_integration_object_PythonInt_toJavaInt(JNIEnv *env, jobject py_int) {
    PyObject *py_obj = object_manager->get_object(env, py_int);
    if (!py_obj) {
        return -1;
    }

    const jint java_int = (jint)PyLong_AsLong(py_obj);

    if (java_int == -1 && PyErr_Occurred()) {
        env->Throw(java_traits<python_exception>::create(env));
        return -1;
    }

    return java_int;
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_from(JNIEnv *env, jclass, jint java_int) {
    PyObject *py_int = PyLong_FromLong((long)java_int);
    if (!py_int) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_int>::convert(env, py_int);
}
