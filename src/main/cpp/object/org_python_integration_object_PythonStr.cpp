#include "org_python_integration_object_PythonStr.h"
#include "globals.h"
#include "traits.h"

JNIEXPORT jstring JNICALL Java_org_python_integration_object_PythonStr_toJavaString(JNIEnv *env, jobject java_object) {
    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    const char *str = PyUnicode_AsUTF8(py_object);
    if (!str) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }
    jstring java_string = env->NewStringUTF(str);
    return java_string;
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonStr_from(JNIEnv *env, jclass, jstring java_string) {
    if (!java_string) {
        env->Throw(java_traits<native_operation_exception>::create(env, "The conversion string cannot be null"));
        return nullptr;
    }

    const char *str = env->GetStringUTFChars(java_string, nullptr);
    PyObject *py_string = PyUnicode_FromString(str);
    env->ReleaseStringUTFChars(java_string, str);

    return java_traits<python_str>::convert(env, py_string);
}
