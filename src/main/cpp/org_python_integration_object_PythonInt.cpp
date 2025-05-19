#include "headers/org_python_integration_object_PythonInt.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"

JNIEXPORT jint JNICALL Java_org_python_integration_object_PythonInt_toJavaInt(JNIEnv *env, jobject py_int) {
    PyObject *py_obj = object_manager->get_object(env, py_int);
    if (!py_obj) {
        return -1;
    }

    const jint java_int = (jint)PyLong_AsLong(py_obj);

    if (java_int == -1 && PyErr_Occurred()) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return -1;
    }

    return java_int;
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_from(JNIEnv *env, jclass, jint java_int) {
    PyObject *py_int = PyLong_FromLong((long)java_int);
    if (!py_int) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }
    const std::size_t index = object_manager->add_object(py_int);
    return create_python_int(env, index, object_manager->get_scope_id());
}
