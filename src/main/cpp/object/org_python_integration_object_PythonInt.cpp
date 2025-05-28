#include "org_python_integration_object_PythonInt.h"
#include "globals.h"
#include "traits.h"

namespace {
class big_integer {
public:
    static jobject of(JNIEnv *env, const char *string_int) {
        jclass big_integer_class = env->FindClass("java/math/BigInteger");
        jmethodID constructor = env->GetMethodID(big_integer_class, "<init>", "(Ljava/lang/String;)V");
        jstring java_string_int = env->NewStringUTF(string_int);
        return env->NewObject(big_integer_class, constructor, java_string_int);
    }
};
}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_toJavaNumber(JNIEnv *env, jobject py_int) {
    PyObject *py_obj = object_manager->get_object(env, py_int);
    if (!py_obj) {
        return nullptr;
    }

    PyObject *py_str_int = PyObject_Str(py_obj);
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
    return ::big_integer::of(env, string_int);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonInt_from(JNIEnv *env, jclass, jint java_int) {
    PyObject *py_int = PyLong_FromLong((long)java_int);
    if (!py_int) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_int>::convert(env, py_int);
}
