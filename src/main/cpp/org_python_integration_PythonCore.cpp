#include "headers/org_python_integration_PythonCore.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT jobject JNICALL Java_org_python_integration_PythonCore_evaluate(JNIEnv *env, jclass cls, jstring java_repr) {
    const char *repr = env->GetStringUTFChars(java_repr, nullptr);
    PyObject* main_module = PyImport_AddModule("__main__");
    PyObject* pdict = PyModule_GetDict(main_module);
    PyObject* pdict_new = PyDict_New();

    PyObject* py_object = PyRun_String(repr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(java_repr, repr);
    Py_DecRef(pdict_new);
    jlong index = object_manager->add_object(py_object);

    return create_python_object(env, index);
}



JNIEXPORT void JNICALL Java_org_python_integration_PythonCore_free(JNIEnv *env, jclass cls, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    object_manager->free_object(index);
}
