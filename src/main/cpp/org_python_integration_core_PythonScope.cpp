#include "headers/org_python_integration_core_PythonScope.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_initializeScope(JNIEnv *env, jobject obj) {
    object_manager = new PythonObjectManager(object_manager, object_manager->get_object_manager_scope() + 1);
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_finalizeScope(JNIEnv *env, jobject java_object) {
    PythonObjectManager *curr_object_manager = object_manager;
    object_manager = curr_object_manager->get_prev_object_manager();
    delete curr_object_manager;
    curr_object_manager = nullptr;
}
