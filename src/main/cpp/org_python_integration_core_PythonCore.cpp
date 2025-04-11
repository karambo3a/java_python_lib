#include "headers/org_python_integration_core_PythonCore.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_evaluate(JNIEnv *env, jclass cls, jstring java_repr) {
    PyObject* main_module = PyImport_AddModule("__main__");
    if (!main_module) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject* pdict = PyModule_GetDict(main_module);
    if (!pdict) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject* pdict_new = PyDict_New();
    if (!pdict_new) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    const char *repr = env->GetStringUTFChars(java_repr, nullptr);
    PyObject* py_object = PyRun_String(repr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(java_repr, repr);

    if (!py_object) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(pdict_new);
        env->Throw(java_exception);
        return nullptr;
    }

    return convert_to_java_object(env, py_object);
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonCore_free(JNIEnv *env, jclass cls, jobject java_object) {
    object_manager->free_object(env, java_object);
}
