#include "headers/org_example_Main.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include <iostream>
#include <Python.h>

std::size_t get_index(JNIEnv *env, jobject java_object) {
    jclass cls = env->GetObjectClass(java_object);
    jmethodID getIndex = env->GetMethodID(cls, "getIndex", "()J");
    jlong index = env->CallLongMethod(java_object, getIndex);
    return (std::size_t)index;
}


JNIEXPORT jobject JNICALL Java_org_example_Main_evaluate(JNIEnv *env, jobject obj, jstring str) {
    const char *cstr = env->GetStringUTFChars(str, nullptr);
    PyObject* main_module = PyImport_AddModule("__main__");
    PyObject* pdict = PyModule_GetDict(main_module);
    PyObject* pdict_new = PyDict_New();

    PyObject* py_object = PyRun_String(cstr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(str, cstr);
    Py_DecRef(pdict_new);

    jclass cls = env->FindClass("org/example/PythonObject");
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jlong index = object_manager->add_object(py_object);
    jobject java_object = env->NewObject(cls, constructor, index);

    return java_object;
}


JNIEXPORT jstring JNICALL Java_org_example_Main_representation(JNIEnv *env, jobject obj, jobject java_object) {
    std::size_t index = get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(index);
    const char *result = PyUnicode_AsUTF8(PyObject_Repr(py_object));
    jstring jresult = env->NewStringUTF(result);
    return jresult;
}


JNIEXPORT jobject JNICALL Java_org_example_Main_getAttribute(JNIEnv *env, jobject obj, jobject java_object, jstring name) {
    const char *attr_name = env->GetStringUTFChars(name, nullptr);
    std::size_t index = get_index(env, java_object);
    PyObject* pyObj = object_manager->get_object(index);

    PyObject* attr_object = PyObject_GetAttrString(pyObj, attr_name);
    if (attr_object == nullptr) {
        return nullptr;
    }

    jclass cls = env->FindClass("org/example/PythonObject");
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    std::size_t addr_attr = object_manager->add_object(attr_object);
    jobject java_obj = env->NewObject(cls, constructor, addr_attr);
    return java_obj;
}


JNIEXPORT jobject JNICALL Java_org_example_Main_callFunction(JNIEnv *env, jobject obj, jobject java_object, jobjectArray jargs) {
    std::size_t index = get_index(env, java_object);
    PyObject* attr_object = object_manager->get_object(index);

    jsize args_cnt = env->GetArrayLength(jargs);
    PyObject* func_result;
    if (args_cnt == 0) {
        func_result = PyObject_CallNoArgs(attr_object);
    } else {
        PyObject *tuple = PyTuple_New((Py_ssize_t)args_cnt);
        for (int i = 0; i < args_cnt; ++i) {
            std::size_t index = get_index(env, env->GetObjectArrayElement(jargs, i));
            PyObject* item = object_manager->get_object(index);
            Py_IncRef(item);
            PyTuple_SetItem(tuple, i, item);
        }
        func_result = PyObject_Call(attr_object, tuple, nullptr);
    }

    std::size_t res_index = object_manager->add_object(func_result);
    jclass cls = env->FindClass("org/example/PythonObject");
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jobject java_obj = env->NewObject(cls, constructor, res_index);
    return java_obj;
}


JNIEXPORT void JNICALL Java_org_example_Main_free(JNIEnv *env, jobject obj, jobject java_object) {
    std::size_t index = get_index(env, java_object);
    object_manager->free_object(index);
}


JNIEXPORT jint JNICALL Java_org_example_Main_asInt(JNIEnv *env, jobject obj, jobject py_int) {
    std::size_t index = get_index(env, py_int);

    PyObject *py_obj = object_manager->get_object(index);

    int java_int = (int)PyLong_AsLong(py_obj);
    return (jint)java_int;
}
