#include "org_python_integration_core_PythonScope.h"
#include "globals.h"
#include "python_object_manager.h"
#include "traits.h"
#include <Python.h>

JNIEXPORT jlong JNICALL Java_org_python_integration_core_PythonScope_initializeScope(JNIEnv *, jobject) {
    initialize_scope();
    return (jlong)object_manager->get_scope_id();
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonScope_finalizeScope(JNIEnv *env, jobject java_object) {
    if (get_scope(env, java_object) != object_manager->get_scope_id()) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Cannot close non-last scope"));
        return;
    }
    finalize_scope();
}
