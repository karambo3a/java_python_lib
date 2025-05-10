#include "headers/org_python_integration_core_PythonScope.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include <Python.h>


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_initializeScope(JNIEnv *env, jobject obj) {
    initialize_scope();
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_finalizeScope(JNIEnv *env, jobject java_object) {
    finalize_scope();
}
