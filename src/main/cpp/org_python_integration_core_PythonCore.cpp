#include "headers/org_python_integration_core_PythonCore.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_evaluate(JNIEnv *env, jclass cls, jstring java_repr) {
    const char *repr = env->GetStringUTFChars(java_repr, nullptr);
    PyObject* main_module = PyImport_AddModule("__main__");
    PyObject* pdict = PyModule_GetDict(main_module);
    PyObject* pdict_new = PyDict_New();

    PyObject* py_object = PyRun_String(repr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(java_repr, repr);
    Py_DecRef(pdict_new);

    if (!py_object) {
        PyObject* py_exception = PyErr_GetRaisedException();
        jthrowable java_exception = create_python_exception(env, py_exception);
        Py_DecRef(py_exception);
        env->Throw(java_exception);
    }

    jlong index = object_manager->add_object(py_object);
    return create_python_object(env, index);
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonCore_free(JNIEnv *env, jclass cls, jobject java_object) {
    object_manager->free_object(env, java_object);
}
