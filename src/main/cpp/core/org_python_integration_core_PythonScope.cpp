#include "../headers/org_python_integration_core_PythonScope.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"
#include "../headers/python_object_manager.h"
#include <Python.h>

JNIEXPORT jlong JNICALL Java_org_python_integration_core_PythonScope_initializeScope(JNIEnv *, jobject) {
    initialize_scope();
    return (jlong)object_manager->get_scope_id();
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_finalizeScope(JNIEnv *env, jobject java_object) {
    if (get_scope(env, java_object) != object_manager->get_scope_id()) {
        jthrowable java_exception = create_native_operation_exception(env, "Cannot close non-last scope");
        env->Throw(java_exception);
        return;
    }
    finalize_scope();
}
