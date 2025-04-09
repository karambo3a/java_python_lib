#ifndef PYTHON_OBJECT_FACTORY_H
#define PYTHON_OBJECT_FACTORY_H

#include <jni.h>
#include <Python.h>

jobject create_python_object(JNIEnv* env, std::size_t index);

jobject create_python_int(JNIEnv* env, std::size_t index);

jobject create_python_callable(JNIEnv* env, std::size_t index);

jthrowable create_python_exception(JNIEnv* env);

jthrowable create_native_operation_exception(JNIEnv* env, const char* message);

#endif // PYTHON_FACTORY_H
