#ifndef PYTHON_OBJECT_FACTORY_H
#define PYTHON_OBJECT_FACTORY_H

#include <jni.h>
#include <Python.h>

jobject create_python_object(JNIEnv* env, std::size_t index);

jobject create_python_int(JNIEnv* env, std::size_t index);

jobject create_python_callable(JNIEnv* env, std::size_t index);


#endif // PYTHON_FACTORY_H
