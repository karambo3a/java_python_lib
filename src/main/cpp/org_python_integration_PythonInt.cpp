#include "headers/org_python_integration_PythonInt.h"
#include "headers/globals.h"


JNIEXPORT jint JNICALL Java_org_python_integration_PythonInt_toJavaInt(JNIEnv *env, jobject py_int){
    PyObject *py_obj = object_manager->get_object(env, py_int);

    return (jint)PyLong_AsLong(py_obj);
}
