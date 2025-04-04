#include "headers/org_python_integration_object_AbstractPythonObject.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include "headers/exceptions.h"
#include <iostream>


JNIEXPORT jstring JNICALL Java_org_python_integration_object_AbstractPythonObject_representation(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    PyObject* py_repr = PyObject_Repr(py_object);
    if (!py_repr) {
        PyObject* py_exception = PyErr_GetRaisedException();
        jthrowable java_exception = create_python_exception(env, py_exception);
        Py_DecRef(py_exception);
        Py_DecRef(py_repr);
        env->Throw(java_exception);
        return nullptr;
    }

    //TODO
    const char *repr = PyUnicode_AsUTF8(py_repr);
    if (!repr) {
        Py_DecRef(py_repr);
        throw_native_operation_exception(env, "Failed to convert Python string to const char*");
        return nullptr;
    }
    jstring java_result = env->NewStringUTF(repr);
    Py_DecRef(py_repr);
    return java_result;
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_getAttribute(JNIEnv *env, jobject java_object, jstring name) {
    if (!name) {
        throw_native_operation_exception(env, "Attribute name cannot be null");
        return nullptr;
    }

    const char *attr_name = env->GetStringUTFChars(name, nullptr);
    if (!attr_name){
        throw_native_operation_exception(env, "Failed to convert Java string to const char*");
        return nullptr;
    }

    PyObject* py_object = object_manager->get_object(env, java_object);

    PyObject* attr_object = PyObject_GetAttrString(py_object, attr_name);
    env->ReleaseStringUTFChars(name, attr_name);

    if (!attr_object) {
        PyObject* py_exception = PyErr_GetRaisedException();
        jthrowable java_exception = create_python_exception(env, py_exception);
        Py_DecRef(py_exception);
        env->Throw(java_exception);
        return nullptr;
    }

    std::size_t attr_index = object_manager->add_object(attr_object);
    return create_python_object(env, attr_index);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asCallable(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyCallable_Check(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_callable = create_python_callable(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_callable);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asInt(JNIEnv *env, jobject java_object){
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyLong_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_int = create_python_int(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_int);
}
