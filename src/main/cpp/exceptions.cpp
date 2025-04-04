#include "headers/exceptions.h"
#include "headers/python_object_factory.h"

void throw_native_operation_exception(JNIEnv *env, const char *message) {
    jclass native_operation_exception = env->FindClass("org/python/integration/exception/NativeOperationException");
    env->ThrowNew(native_operation_exception, message);
}
