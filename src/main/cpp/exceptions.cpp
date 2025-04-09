#include "headers/exceptions.h"
#include "headers/python_object_factory.h"

void throw_native_operation_exception(JNIEnv *env, const char *message) {
    jthrowable java_exception = create_native_operation_exception(env, message);
    env->Throw(java_exception);
}

void throw_python_exception(JNIEnv *env, jthrowable java_exception) {
    env->Throw(java_exception);
}
