#include "org_example_Main.h"
#include <iostream>
#include <Python.h>

JNIEXPORT jobject JNICALL Java_org_example_Main_evaluate(JNIEnv *env, jobject obj, jstring str) {
    Py_Initialize();
    const char *cstr = env->GetStringUTFChars(str, nullptr);
    PyObject *pyObj = PyUnicode_FromString(cstr);
    env->ReleaseStringUTFChars(str, cstr);

    Py_INCREF(pyObj);

    jclass cls = env->FindClass("org/example/PythonObj");
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jlong addr = reinterpret_cast<jlong>(pyObj);
    jobject javaObj = env->NewObject(cls, constructor, addr);
    return javaObj;
}

JNIEXPORT jstring JNICALL Java_org_example_Main_representation(JNIEnv *env, jobject obj, jobject javaObj) {
    jclass cls = env->GetObjectClass(javaObj);
    jmethodID getAddr = env->GetMethodID(cls, "getAddr", "()J");
    jlong addr = env->CallLongMethod(javaObj, getAddr);
    PyObject *pyObj = reinterpret_cast<PyObject*>(addr);
    const char *result = PyUnicode_AsUTF8(pyObj);
    jstring jresult = env->NewStringUTF(result);
    Py_DECREF(pyObj);
    Py_Finalize();
    return jresult;
}
