#include "org_python_integration_object_PythonBool.h"
#include "gil.h"
#include "globals.h"
#include "traits.h"

JNIEXPORT jboolean JNICALL Java_org_python_integration_object_PythonBool_toJavaBoolean(JNIEnv *env, jobject java_object) {
    const GIL gil;

    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return JNI_FALSE;
    }

    const int is_true = PyObject_IsTrue(py_object);
    if (is_true == -1) {
        env->Throw(java_traits<python_exception>::create(env));
    }
    return (jboolean)is_true;
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonBool_from(JNIEnv *env, jclass, jboolean java_boolean) {
    const GIL gil;

    PyObject *py_bool = PyBool_FromLong((long)java_boolean);
    if (!py_bool) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_bool>::convert(env, py_bool);
}
