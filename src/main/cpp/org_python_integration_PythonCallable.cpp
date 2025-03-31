#include "headers/org_python_integration_PythonCallable.h"
#include "headers/globals.h"
#include <iostream>

JNIEXPORT jobject JNICALL Java_org_python_integration_PythonCallable_call(JNIEnv *env, jobject java_object, jobjectArray jargs) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* attr_object = object_manager->get_object(index);

    jsize args_cnt = env->GetArrayLength(jargs);
    PyObject* func_result;
    if (args_cnt == 0) {
        func_result = PyObject_CallNoArgs(attr_object);
    } else {
        PyObject *tuple = PyTuple_New((Py_ssize_t)args_cnt);
        for (int i = 0; i < args_cnt; ++i) {
            std::size_t index = object_manager->get_index(env, env->GetObjectArrayElement(jargs, i));
            PyObject* item = object_manager->get_object(index);
            Py_IncRef(item);
            PyTuple_SetItem(tuple, i, item);
        }
        func_result = PyObject_Call(attr_object, tuple, nullptr);
    }

    std::size_t res_index = object_manager->add_object(func_result);
    jclass cls = env->FindClass("org/python/integration/PythonObject");
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jobject java_obj = env->NewObject(cls, constructor, res_index);
    return java_obj;
}
