#include "headers/org_python_integration_object_PythonStr.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"


JNIEXPORT jstring JNICALL Java_org_python_integration_object_PythonStr_toJavaString(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    const char *str = PyUnicode_AsUTF8(py_object);
    if (!str) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }
    jstring java_string = env->NewStringUTF(str);
    return java_string;
}
