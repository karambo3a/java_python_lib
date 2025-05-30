#include "org_python_integration_object_PythonFloat.h"
#include "globals.h"
#include "traits.h"
#include <Python.h>
#include <jni.h>

JNIEXPORT jdouble JNICALL
Java_org_python_integration_object_PythonFloat_toJavaDouble(JNIEnv *env, jobject java_object) {
    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return -1.0;
    }

    const jdouble java_double = (jdouble)PyFloat_AsDouble(py_object);

    if (java_double == -1.0 && PyErr_Occurred()) {
        env->Throw(java_traits<python_exception>::create(env));
        return -1.0;
    }

    return java_double;
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_PythonFloat_from(JNIEnv *env, jclass, jdouble java_double) {
    PyObject *py_float = PyFloat_FromDouble((double)java_double);
    if (!py_float) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_float>::convert(env, py_float);
}
