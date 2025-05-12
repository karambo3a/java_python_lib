#include "headers/org_python_integration_core_PythonScope.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include <Python.h>


JNIEXPORT jlong JNICALL Java_org_python_integration_core_PythonScope_initializeScope(JNIEnv *env, jobject) {
    object_manager = new PythonObjectManager(object_manager, object_manager->get_scope_id() + 1);
    return (jlong) object_manager->get_scope_id();
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_finalizeScope(JNIEnv *env, jobject java_object) {
    if (get_scope(env, java_object) != object_manager->get_scope_id()) {
        jthrowable java_exception = create_native_operation_exception(env, "Cannot close non-last scope");
        env->Throw(java_exception);
        return;
    }
    PythonObjectManager *curr_object_manager = object_manager;
    object_manager = curr_object_manager->get_prev_object_manager();
    delete curr_object_manager;
    curr_object_manager = nullptr;
}
