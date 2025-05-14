#include "headers/globals.h"
#include "headers/python_object_manager.h"
#include <cstddef>
#include <jni.h>

PythonObjectManager *object_manager = nullptr;

std::size_t get_index(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "index", "J");
    const jlong index = env->GetLongField(java_object, field);
    return (std::size_t)index;
}

std::size_t get_scope(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jfieldID field = env->GetFieldID(cls, "scopeId", "J");
    const jlong scope_id = env->GetLongField(java_object, field);
    return (std::size_t)scope_id;
}
