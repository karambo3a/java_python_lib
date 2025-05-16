#include "headers/org_python_integration_object_PythonSet.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include "headers/python_object_manager.h"
#include <Python.h>

namespace {
    jobject set_iterator(JNIEnv *env, jobject java_set) {
        jclass cls = env->FindClass("java/util/Set");
        jmethodID iterator = env->GetMethodID(cls, "iterator", "()Ljava/util/Iterator;");
        return env->CallObjectMethod(java_set, iterator);
    }

    bool set_has_next(JNIEnv *env, jobject iterator) {
        jclass cls = env->FindClass("java/util/Iterator");
        jmethodID has_next_method = env->GetMethodID(cls, "hasNext", "()Z");
        return (bool)env->CallBooleanMethod(iterator, has_next_method);
    }

    jobject set_next(JNIEnv *env, jobject iterator) {
        jclass cls = env->FindClass("java/util/Iterator");
        jmethodID next_method = env->GetMethodID(cls, "next", "()Ljava/lang/Object;");
        return env->CallObjectMethod(iterator, next_method);
    }
}



JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonSet_from(JNIEnv *env, jclass, jobject java_set) {
    if (!java_set) {
        jthrowable java_exception = create_native_operation_exception(env, "Java set cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jobject iterator = set_iterator(env, java_set);
    PyObject *py_set = PySet_New(nullptr);

    while (set_has_next(env, iterator)) {
        jobject java_object = set_next(env, iterator);
        PyObject *py_object = object_manager->get_object(env, java_object);
        if (!py_object) {
            Py_DecRef(py_set);
            return nullptr;
        }

        Py_IncRef(py_object);
        if (PySet_Add(py_set, py_object) < 0) {
            jthrowable java_exception = create_python_exception(env);
            env->Throw(java_exception);
            Py_DecRef(py_set);
            Py_DecRef(py_object);
            return nullptr;
        }
    }

    const std::size_t index = object_manager->add_object(py_set);
    return create_python_set(env, index, object_manager->get_scope_id());
}
