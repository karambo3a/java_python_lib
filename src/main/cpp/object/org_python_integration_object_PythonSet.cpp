#include "org_python_integration_object_PythonSet.h"
#include "globals.h"
#include "python_object_manager.h"
#include "traits.h"
#include <Python.h>

namespace {
class iterator {
public:
    iterator(JNIEnv *env, jobject java_iterator) : env(env), java_iterator(java_iterator), current_value(nullptr) {
        if (this->java_iterator && this->has_next()) {
            this->current_value = this->next();
        }
    }

    ::iterator &operator++() {
        if (this->java_iterator && this->has_next()) {
            this->current_value = this->next();
        } else {
            this->java_iterator = nullptr;
            this->current_value = nullptr;
        }
        return *this;
    }

    bool operator!=(const iterator &other) const {
        return this->java_iterator != other.java_iterator;
    }

    jobject &operator*() {
        return this->current_value;
    }

private:
    JNIEnv *env;
    jobject java_iterator;
    jobject current_value;

    bool has_next() {
        jclass cls = env->FindClass("java/util/Iterator");
        jmethodID has_next_method = env->GetMethodID(cls, "hasNext", "()Z");
        return (bool)env->CallBooleanMethod(java_iterator, has_next_method);
    }

    jobject next() {
        jclass cls = env->FindClass("java/util/Iterator");
        jmethodID next_method = env->GetMethodID(cls, "next", "()Ljava/lang/Object;");
        return env->CallObjectMethod(java_iterator, next_method);
    }
};

class set {
public:
    set(JNIEnv *env, jobject java_set) : env(env), java_set(java_set) {
    }

    ::iterator begin() {
        return this->iterator();
    }

    ::iterator end() {
        return ::iterator(env, nullptr);
    }

private:
    JNIEnv *env;
    jobject java_set;

    ::iterator iterator() {
        jclass cls = env->FindClass("java/util/Set");
        jmethodID iterator = env->GetMethodID(cls, "iterator", "()Ljava/util/Iterator;");
        return ::iterator(env, env->CallObjectMethod(java_set, iterator));
    }
};
}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonSet_from(JNIEnv *env, jclass, jobject java_set) {
    if (!java_set) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java set cannot be null"));
        return nullptr;
    }

    PyObject *py_set = PySet_New(nullptr);

    for (auto &value : set(env, java_set)) {
        PyObject *py_object = object_manager->get_object(env, value);
        if (!py_object) {
            Py_DecRef(py_set);
            return nullptr;
        }

        Py_IncRef(py_object);
        if (PySet_Add(py_set, py_object) < 0) {
            env->Throw(java_traits<python_exception>::create(env));
            Py_DecRef(py_set);
            Py_DecRef(py_object);
            return nullptr;
        }
    }

    return java_traits<python_set>::convert(env, py_set);
}
