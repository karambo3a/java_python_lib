#include "headers/org_python_integration_object_AbstractPythonObject.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <iostream>

JNIEXPORT jboolean JNICALL Java_org_python_integration_object_AbstractPythonObject_equals(JNIEnv *env, jobject this_object, jobject other_object){
    if (this_object == other_object) {
        return JNI_TRUE;
    }
    if (!env->IsInstanceOf(other_object, env->GetObjectClass(this_object))) {
        return JNI_FALSE;
    }
    PyObject* py_this = object_manager->get_object(env, this_object);
    PyObject* py_other = object_manager->get_object(env, other_object);
    if (!py_this || !py_other) {
        return JNI_FALSE;
    }
    int result = PyObject_RichCompareBool(py_this, py_other, Py_EQ);
    if (result == -1) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return JNI_FALSE;
    } else if (result == 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL Java_org_python_integration_object_AbstractPythonObject_hashCode(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return -1;
    }
    Py_hash_t hash = PyObject_Hash(py_object);
    if (hash == -1 && PyErr_Occurred()) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
    }
    return (jint)hash;
}


JNIEXPORT jstring JNICALL Java_org_python_integration_object_AbstractPythonObject_toString(JNIEnv *env, jobject java_object) {
    PyObject* py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    PyObject* py_str = PyObject_Str(py_object);
    if (!py_str) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
    }
    const char *repr = PyUnicode_AsUTF8(py_str);
    jstring java_string = nullptr;
    if (!repr) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(py_object);
        env->Throw(java_exception);
    } else {
        java_string = env->NewStringUTF(repr);
    }
    Py_DecRef(py_str);
    return java_string;
}


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
    jstring java_string = env->NewStringUTF(repr);
    Py_DecRef(py_repr);
    return java_string;
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


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asDict(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyDict_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_dict = create_python_dict(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_dict);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asTuple(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PyTuple_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_tuple = create_python_tuple(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_tuple);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_AbstractPythonObject_asSet(JNIEnv *env, jobject java_object) {
    std::size_t index = object_manager->get_index(env, java_object);
    PyObject* py_object = object_manager->get_object(env, index);
    if (!py_object) {
        return nullptr;
    }

    jclass optional_class = env->FindClass("java/util/Optional");
    if (!PySet_CheckExact(py_object)) {
        jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
        return env->CallStaticObjectMethod(optional_class, empty_method);
    }
    jobject java_py_set = create_python_set(env, index);

    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_set);
}

