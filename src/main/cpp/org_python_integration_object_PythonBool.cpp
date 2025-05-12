#include "headers/org_python_integration_object_PythonBool.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"


JNIEXPORT jboolean JNICALL Java_org_python_integration_object_PythonBool_toJavaBoolean(JNIEnv *env, jobject java_object){
    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return (jboolean)false;
    }

    int is_true = PyObject_IsTrue(py_object);
    if (is_true == -1) {
        jthrowable java_exception = create_native_operation_exception(env, "Failed to convert Python int to Java int");
        env->Throw(java_exception);
    }
    return (jboolean)is_true;
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonBool_from(JNIEnv *env, jclass java_cls, jboolean java_boolean) {
    PyObject *py_bool = PyBool_FromLong((long)java_boolean);
    if (!py_bool) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }
    std::size_t index = object_manager->add_object(py_bool);
    return create_python_bool(env, index, object_manager->get_object_manager_scope());
}
