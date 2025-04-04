#include "headers/org_python_integration_object_PythonInt.h"
#include "headers/exceptions.h"
#include "headers/globals.h"


JNIEXPORT jint JNICALL Java_org_python_integration_object_PythonInt_toJavaInt(JNIEnv *env, jobject py_int){
    PyObject *py_obj = object_manager->get_object(env, py_int);

    jint java_int = (jint)PyLong_AsLong(py_obj);

    if (java_int == -1 && PyErr_Occurred()) {
        throw_native_operation_exception(env, "Failed to convert Python int to Java int");
        return -1;
    }

    return java_int;
}
