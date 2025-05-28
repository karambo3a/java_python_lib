#ifndef TRAITS_H
#define TRAITS_H

#include "traits.h"
#include <jni.h>

struct python_object;
struct python_callable;
struct python_int;
struct python_bool;
struct python_str;
struct python_list;
struct python_dict;
struct python_tuple;
struct python_set;
struct python_exception;
struct native_operation_exception;


template <typename T>
struct python_traits;

template <>
struct python_traits<python_callable> {
    static bool check(PyObject *py_object) {
        return PyCallable_Check(py_object);
    }
};

template <>
struct python_traits<python_int> {
    static bool check(PyObject *py_object) {
        return PyLong_CheckExact(py_object);
    }
};

template <>
struct python_traits<python_bool> {
    static bool check(PyObject *py_object) {
        return PyBool_Check(py_object);
    }
};

template <>
struct python_traits<python_str> {
    static bool check(PyObject *py_object) {
        return PyUnicode_CheckExact(py_object);
    }
};

template <>
struct python_traits<python_list> {
    static bool check(PyObject *py_object) {
        return PyList_CheckExact(py_object);
    }
};

template <>
struct python_traits<python_dict> {
    static bool check(PyObject *py_object) {
        return PyDict_CheckExact(py_object);
    }
};

template <>
struct python_traits<python_tuple> {
    static bool check(PyObject *py_object) {
        return PyTuple_CheckExact(py_object);
    }
};

template <>
struct python_traits<python_set> {
    static bool check(PyObject *py_object) {
        return PySet_CheckExact(py_object);
    }
};

template <typename T>
struct java_traits;

namespace {
jobject create_java_object(JNIEnv *env, jclass cls, std::size_t index, std::size_t scope) {
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(JJ)V");
    jobject java_py_object = env->NewObject(cls, constructor, index, scope);
    return java_py_object;
}

template <typename T>
jobject convert_toT(JNIEnv *env, PyObject *py_object, bool is_borrowed) {
    if (!py_object) {
        return nullptr;
    }
    const std::size_t index = object_manager->add_object(py_object, is_borrowed);
    return java_traits<T>::create(env, index, object_manager->get_scope_id());
}
}  // namespace

template <>
struct java_traits<python_object> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_object_class = env->FindClass("org/python/integration/object/PythonObject");
        return create_java_object(env, python_object_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_object, bool is_borrowed = false) {
        return convert_toT<python_object>(env, py_object, is_borrowed);
    }
};

template <>
struct java_traits<python_callable> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_callable_class = env->FindClass("org/python/integration/object/PythonCallable");
        return create_java_object(env, python_callable_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_callable, bool is_borrowed = false) {
        return convert_toT<python_callable>(env, py_callable, is_borrowed);
    }
};

template <>
struct java_traits<python_int> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_int_class = env->FindClass("org/python/integration/object/PythonInt");
        return create_java_object(env, python_int_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_int, bool is_borrowed = false) {
        return convert_toT<python_int>(env, py_int, is_borrowed);
    }
};

template <>
struct java_traits<python_bool> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_bool_class = env->FindClass("org/python/integration/object/PythonBool");
        return create_java_object(env, python_bool_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_bool, bool is_borrowed = false) {
        return convert_toT<python_bool>(env, py_bool, is_borrowed);
    }
};

template <>
struct java_traits<python_str> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_str_class = env->FindClass("org/python/integration/object/PythonStr");
        return create_java_object(env, python_str_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_str, bool is_borrowed = false) {
        return convert_toT<python_str>(env, py_str, is_borrowed);
    }
};

template <>
struct java_traits<python_list> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_list_class = env->FindClass("org/python/integration/object/PythonList");
        return create_java_object(env, python_list_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_list, bool is_borrowed = false) {
        return convert_toT<python_list>(env, py_list, is_borrowed);
    }
};

template <>
struct java_traits<python_dict> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_list_class = env->FindClass("org/python/integration/object/PythonDict");
        return create_java_object(env, python_list_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_dict, bool is_borrowed = false) {
        return convert_toT<python_dict>(env, py_dict, is_borrowed);
    }
};

template <>
struct java_traits<python_tuple> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_tuple_class = env->FindClass("org/python/integration/object/PythonTuple");
        return create_java_object(env, python_tuple_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_tuple, bool is_borrowed = false) {
        return convert_toT<python_tuple>(env, py_tuple, is_borrowed);
    }
};

template <>
struct java_traits<python_set> {
    static jobject create(JNIEnv *env, std::size_t index, std::size_t scope_id) {
        jclass python_set_class = env->FindClass("org/python/integration/object/PythonSet");
        return create_java_object(env, python_set_class, index, scope_id);
    }

    static jobject convert(JNIEnv *env, PyObject *py_set, bool is_borrowed = false) {
        return convert_toT<python_set>(env, py_set, is_borrowed);
    }
};

template <>
struct java_traits<python_exception> {
    static jthrowable create(JNIEnv *env) {
        PyObject *py_type = nullptr, *py_value = nullptr, *py_traceback = nullptr;
#if PYTHON_VERSION >= 312
        py_value = PyErr_GetRaisedException();
#else
        PyErr_Fetch(&py_type, &py_value, &py_traceback);
        PyErr_NormalizeException(&py_type, &py_value, &py_traceback);
#endif

        jobject java_value = java_traits<python_object>::convert(env, py_value);

        jclass python_exception_class = env->FindClass("org/python/integration/exception/PythonException");
        jmethodID constructor =
            env->GetMethodID(python_exception_class, "<init>", "(Lorg/python/integration/object/IPythonObject;)V");
        jthrowable java_exception = (jthrowable)env->NewObject(python_exception_class, constructor, java_value);
        Py_XDECREF(py_type);
        Py_XDECREF(py_traceback);
        return java_exception;
    }
};

template <>
struct java_traits<native_operation_exception> {
    static jthrowable create(JNIEnv *env, const char *message) {
        jclass python_exception_class = env->FindClass("org/python/integration/exception/NativeOperationException");
        jmethodID constructor = env->GetMethodID(python_exception_class, "<init>", "(Ljava/lang/String;)V");
        jthrowable java_exception =
            (jthrowable)env->NewObject(python_exception_class, constructor, env->NewStringUTF(message));
        return java_exception;
    }
};

#endif  // TRAITS_H
