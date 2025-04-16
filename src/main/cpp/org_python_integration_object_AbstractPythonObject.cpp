#include "headers/org_python_integration_object_AbstractPythonObject.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <iostream>


JNIEXPORT jstring JNICALL Java_org_python_integration_object_AbstractPythonObject_representation(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    PyObject* py_repr = PyObject_Repr(py_object);
    if (!py_repr) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    // TODO: change PyUnicode_AsUTF8 to PyUnicode_AsUTF8AndSize
    const char *repr = PyUnicode_AsUTF8(py_repr);
    if (!repr) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(py_repr);
        env->Throw(java_exception);
        return nullptr;
    }
    jstring java_result = env->NewStringUTF(repr);
    Py_DecRef(py_repr);
    return java_result;
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_getAttribute(JNIEnv *env, jobject java_object, jstring name) {
    if (!name) {
        jthrowable java_exception = create_native_operation_exception(env, "Attribute name cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }

    const char *attr_name = env->GetStringUTFChars(name, nullptr);
    if (!attr_name){
        jthrowable java_exception = create_native_operation_exception(env, "Failed to convert Java string to const char*");
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject* attr_object = PyObject_GetAttrString(py_object, attr_name);
    env->ReleaseStringUTFChars(name, attr_name);

    if (!attr_object) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    return convert_to_java_object(env, attr_object);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asCallable(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

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
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyLong_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_int = create_python_int(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_int);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asBool(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyBool_Check(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_bool = create_python_bool(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_bool);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asList(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyList_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_list = create_python_list(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_list);
}
