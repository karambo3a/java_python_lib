#include "headers/org_python_integration_AbstractPythonObject.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <iostream>

JNIEXPORT jstring JNICALL Java_org_python_integration_AbstractPythonObject_representation(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    PyObject* py_repr = PyObject_Repr(py_object);
    //TODO
    const char *repr = PyUnicode_AsUTF8(PyObject_Repr(py_object));
    jstring java_result = env->NewStringUTF(repr);
    Py_DecRef(py_repr);
    return java_result;
}


JNIEXPORT jobject JNICALL Java_org_python_integration_AbstractPythonObject_getAttribute(JNIEnv *env, jobject java_object, jstring name) {
    const char *attr_name = env->GetStringUTFChars(name, nullptr);
    PyObject* pyObj = object_manager->get_object(env, java_object);
    PyObject* attr_object = PyObject_GetAttrString(pyObj, attr_name);
    env->ReleaseStringUTFChars(name, attr_name);

    if (attr_object == nullptr) {
        return nullptr;
    }
    std::size_t attr_index = object_manager->add_object(attr_object);

    return create_python_object(env, attr_index);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_AbstractPythonObject_asCallable(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(index);

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyCallable_Check(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_callable = create_python_callable(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_callable);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_AbstractPythonObject_asInt(JNIEnv *env, jobject java_object){
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(index);

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyLong_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_int = create_python_int(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_int);
}
