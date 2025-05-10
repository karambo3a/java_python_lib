#ifndef PYTHON_OBJECT_FACTORY_H
#define PYTHON_OBJECT_FACTORY_H

#include <jni.h>
#include <Python.h>

jobject create_python_object(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_int(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_callable(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_bool(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_str(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_list(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_dict(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_tuple(JNIEnv* env, std::size_t index, std::size_t scope);

jobject create_python_set(JNIEnv* env, std::size_t index, std::size_t scope);

jobject convert_to_python_object(JNIEnv* env, PyObject* py_object);

jobject convert_to_python_callable(JNIEnv *env, PyObject *py_callable);

jthrowable create_python_exception(JNIEnv* env);

jthrowable create_native_operation_exception(JNIEnv* env, const char* message);

#endif // PYTHON_FACTORY_H
