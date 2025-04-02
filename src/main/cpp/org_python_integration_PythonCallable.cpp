#include "headers/org_python_integration_PythonCallable.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include <iostream>

JNIEXPORT jobject JNICALL Java_org_python_integration_PythonCallable_call(JNIEnv *env, jobject java_object, jobjectArray jargs) {
    PyObject* callable_object = object_manager->get_object(env, java_object);

    jsize args_cnt = env->GetArrayLength(jargs);
    PyObject* func_result;
    if (args_cnt == 0) {
        func_result = PyObject_CallNoArgs(callable_object);
    } else {
        PyObject *args = PyTuple_New((Py_ssize_t)args_cnt);
        for (int i = 0; i < args_cnt; ++i) {
            PyObject* arg = object_manager->get_object(env, env->GetObjectArrayElement(jargs, i));
            Py_IncRef(arg);
            PyTuple_SetItem(args, i, arg);
        }
        func_result = PyObject_Call(callable_object, args, nullptr);
        Py_DecRef(args);
    }

    std::size_t res_index = object_manager->add_object(func_result);
    return create_python_object(env, res_index);
}
