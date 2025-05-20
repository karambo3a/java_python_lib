#include "../headers/org_python_integration_object_PythonStr.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"

JNIEXPORT jstring JNICALL Java_org_python_integration_object_PythonStr_toJavaString(JNIEnv *env, jobject java_object) {
    PyObject *py_object = object_manager->get_object(env, java_object);
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

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonStr_from(JNIEnv *env, jclass, jstring java_string) {
    if (!java_string) {
        jthrowable java_exception = create_native_operation_exception(env, "The conversion string cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }
    const char *str = env->GetStringUTFChars(java_string, nullptr);
    PyObject *py_string = PyUnicode_FromString(str);
    env->ReleaseStringUTFChars(java_string, str);
    const std::size_t index = object_manager->add_object(py_string);
    return create_python_str(env, index, object_manager->get_scope_id());
}
