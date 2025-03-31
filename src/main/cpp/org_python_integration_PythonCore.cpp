#include "headers/org_python_integration_PythonCore.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT jobject JNICALL Java_org_python_integration_PythonCore_evaluate(JNIEnv *env, jclass cls, jstring repr) {
    const char *cstr = env->GetStringUTFChars(repr, nullptr);
    PyObject* main_module = PyImport_AddModule("__main__");
    PyObject* pdict = PyModule_GetDict(main_module);
    PyObject* pdict_new = PyDict_New();

    PyObject* py_object = PyRun_String(cstr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(repr, cstr);
    Py_DecRef(pdict_new);

    jclass py_object_cls = env->FindClass("org/python/integration/PythonObject");
    jmethodID constructor = env->GetMethodID(py_object_cls, "<init>", "(J)V");
    jlong index = object_manager->add_object(py_object);
    jobject java_object = env->NewObject(py_object_cls, constructor, index);
    return java_object;
}



JNIEXPORT void JNICALL Java_org_python_integration_PythonCore_free(JNIEnv *env, jclass cls, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    object_manager->free_object(index);
}
