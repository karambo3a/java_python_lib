#include "headers/org_python_integration_object_PythonInt.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"


JNIEXPORT jint JNICALL Java_org_python_integration_object_PythonInt_toJavaInt(JNIEnv *env, jobject py_int){
    PyObject *py_obj = object_manager->get_object(env, py_int);
    if (!py_obj) {
        return -1;
    }

    jint java_int = (jint)PyLong_AsLong(py_obj);

    if (java_int == -1 && PyErr_Occurred()) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return -1;
    }

    return java_int;
}
