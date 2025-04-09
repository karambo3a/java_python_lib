#ifndef EXCEPTIONS_H
#define EXCEPTIONS_H

#include <jni.h>

void throw_native_operation_exception(JNIEnv *env, const char *message);

void throw_python_exception(JNIEnv *env, jthrowable java_exception);

#endif // EXCEPTIONS_H
