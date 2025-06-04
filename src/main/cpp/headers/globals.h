#ifndef GLOBALS_H
#define GLOBALS_H

#include "python_object_manager.h"
#include <Python.h>

extern PyThreadState *_save;

extern PythonObjectManager *object_manager;

std::size_t get_index(JNIEnv *env, jobject java_object);

std::size_t get_scope(JNIEnv *env, jobject java_object);

void initialize_scope();

void finalize_scope();

#endif  // GLOBALS_H
