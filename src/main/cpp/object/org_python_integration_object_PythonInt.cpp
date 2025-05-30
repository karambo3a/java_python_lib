#include "org_python_integration_object_PythonInt.h"
#include "globals.h"
#include "traits.h"

namespace {
jobject big_integer_of(JNIEnv *env, const char *string_int) {
    jclass big_integer_class = env->FindClass("java/math/BigInteger");
    jmethodID constructor = env->GetMethodID(big_integer_class, "<init>", "(Ljava/lang/String;)V");
    jstring java_string_int = env->NewStringUTF(string_int);
    return env->NewObject(big_integer_class, constructor, java_string_int);
}
}  // namespace

JNIEXPORT jlong JNICALL Java_org_python_integration_object_PythonInt_toJavaLong(JNIEnv *env, jobject py_int) {
    PyObject *py_object = object_manager->get_object(env, py_int);
    if (!py_object) {
        return -1;
    }

    const jlong java_long = (jlong)PyLong_AsLong(py_object);
    if (java_long == -1 && PyErr_Occurred()) {
        env->Throw(java_traits<python_exception>::create(env));
        return -1;
    }

    return java_long;
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_toJavaBigInteger(JNIEnv *env, jobject py_int) {
    PyObject *py_object = object_manager->get_object(env, py_int);
    if (!py_object) {
        return nullptr;
    }

    PyObject *py_str_int = PyObject_Str(py_object);
    if (!py_str_int) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    const char *string_int = PyUnicode_AsUTF8(py_str_int);
    if (!string_int) {
        env->Throw(java_traits<python_exception>::create(env));
        Py_DecRef(py_str_int);
        return nullptr;
    }
    Py_DecRef(py_str_int);
    return big_integer_of(env, string_int);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_from(JNIEnv *env, jclass, jlong java_long) {
    PyObject *py_int = PyLong_FromLong((long)java_long);
    if (!py_int) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_int>::convert(env, py_int);
}
