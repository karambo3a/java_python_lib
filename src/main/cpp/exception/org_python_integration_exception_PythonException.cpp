#include "org_python_integration_exception_PythonException.h"
#include "gil.h"
#include "globals.h"

JNIEXPORT void JNICALL Java_org_python_integration_exception_PythonException_free(JNIEnv *env, jobject java_object) {
    const GIL gil;

    jclass cls = env->FindClass("org/python/integration/exception/PythonException");
    jfieldID field_value = env->GetFieldID(cls, "value", "Lorg/python/integration/object/IPythonObject;");
    jobject value = env->GetObjectField(java_object, field_value);

    object_manager->free_object(env, value);
}
