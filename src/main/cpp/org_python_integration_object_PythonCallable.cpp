#include "headers/org_python_integration_object_PythonCallable.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include <iostream>


JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_call(JNIEnv *env, jobject java_object, jobjectArray jargs) {
    PyObject* callable_object = object_manager->get_object(env, java_object);
    if (!callable_object) {
        return nullptr;
    }

    jsize args_cnt = env->GetArrayLength(jargs);
    PyObject *args = PyTuple_New((Py_ssize_t)args_cnt);
    if (!args) {
        jthrowable java_exception = create_native_operation_exception(env, "Failed to create tuple for arguments");
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject* func_result;
    if (args_cnt == 0) {
        func_result = PyObject_CallNoArgs(callable_object);
    } else {
        for (int i = 0; i < args_cnt; ++i) {
            PyObject* arg = object_manager->get_object(env, env->GetObjectArrayElement(jargs, i));
            if (!arg) {
                return nullptr;
            }
            Py_IncRef(arg);
            PyTuple_SetItem(args, i, arg);
        }
        func_result = PyObject_Call(callable_object, args, nullptr);
    }

    if (!func_result) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(args);
        env->Throw(java_exception);
        return nullptr;
    }

    return convert_to_java_object(env, func_result);
}
