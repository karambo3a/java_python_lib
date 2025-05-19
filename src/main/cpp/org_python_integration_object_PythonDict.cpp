#include "headers/org_python_integration_object_PythonDict.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include "headers/python_object_manager.h"
#include <Python.h>

namespace {
jobject map_entry_set(JNIEnv *env, jobject java_map) {
    jclass cls = env->FindClass("java/util/Map");
    jmethodID set = env->GetMethodID(cls, "entrySet", "()Ljava/util/Set;");
    return env->CallObjectMethod(java_map, set);
}

jobject map_iterator(JNIEnv *env, jobject java_entry_set) {
    jclass cls = env->FindClass("java/util/Set");
    jmethodID iterator = env->GetMethodID(cls, "iterator", "()Ljava/util/Iterator;");
    return env->CallObjectMethod(java_entry_set, iterator);
}

bool map_has_next(JNIEnv *env, jobject iterator) {
    jclass cls = env->FindClass("java/util/Iterator");
    jmethodID has_next_method = env->GetMethodID(cls, "hasNext", "()Z");
    return (bool)env->CallBooleanMethod(iterator, has_next_method);
}

jobject map_next(JNIEnv *env, jobject iterator) {
    jclass cls = env->FindClass("java/util/Iterator");
    jmethodID next_method = env->GetMethodID(cls, "next", "()Ljava/lang/Object;");
    return env->CallObjectMethod(iterator, next_method);
}

jobject entry_get_key(JNIEnv *env, jobject entry) {
    jclass cls = env->FindClass("java/util/Map$Entry");
    jmethodID get_key_method = env->GetMethodID(cls, "getKey", "()Ljava/lang/Object;");
    return env->CallObjectMethod(entry, get_key_method);
}

jobject entry_get_value(JNIEnv *env, jobject entry) {
    jclass cls = env->FindClass("java/util/Map$Entry");
    jmethodID get_value_method = env->GetMethodID(cls, "getValue", "()Ljava/lang/Object;");
    return env->CallObjectMethod(entry, get_value_method);
}
}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonDict_from(JNIEnv *env, jclass, jobject java_map) {
    if (!java_map) {
        jthrowable java_exception = create_native_operation_exception(env, "Java map cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jobject entry_set = map_entry_set(env, java_map);
    jobject iterator = map_iterator(env, entry_set);
    PyObject *py_dict = PyDict_New();
    if (!py_dict) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    while (map_has_next(env, iterator)) {
        jobject java_entry = map_next(env, iterator);
        jobject java_key = entry_get_key(env, java_entry);
        jobject java_value = entry_get_value(env, java_entry);
        PyObject *py_key = object_manager->get_object(env, java_key);
        if (!py_key) {
            Py_DecRef(py_dict);
            return nullptr;
        }
        PyObject *py_value = object_manager->get_object(env, java_value);
        if (!py_value) {
            Py_DecRef(py_dict);
            return nullptr;
        }

        if (PyDict_SetItem(py_dict, py_key, py_value) < 0) {
            jthrowable java_exception = create_python_exception(env);
            env->Throw(java_exception);
            Py_DecRef(py_dict);
            return nullptr;
        }
    }

    const std::size_t index = object_manager->add_object(py_dict);
    return create_python_dict(env, index, object_manager->get_scope_id());
}
