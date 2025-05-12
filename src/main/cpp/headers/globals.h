#ifndef GLOBALS_H
#define GLOBALS_H

#include "python_object_manager.h"

extern PythonObjectManager* object_manager;

std::size_t get_index(JNIEnv *env, jobject java_object);

std::size_t get_scope(JNIEnv *env, jobject java_object);

#endif // GLOBALS_H
