#include "org_python_integration_object_PythonDict.h"
#include "gil.h"
#include "globals.h"
#include "python_object_manager.h"
#include "traits.h"
#include <Python.h>

namespace {
class entry {
public:
    entry(JNIEnv *env, jobject java_entry) : env(env), java_entry(java_entry) {
        jclass cls = env->FindClass("java/util/Map$Entry");
        get_key_method = env->GetMethodID(cls, "getKey", "()Ljava/lang/Object;");
        get_value_method = env->GetMethodID(cls, "getValue", "()Ljava/lang/Object;");
    }

    jobject key() {
        return env->CallObjectMethod(java_entry, get_key_method);
    }

    jobject value() {
        return env->CallObjectMethod(java_entry, get_value_method);
    }

private:
    JNIEnv *env;
    jobject java_entry;
    jmethodID get_key_method;
    jmethodID get_value_method;
};

class iterator {
public:
    iterator(JNIEnv *env, jobject java_iterator) : env(env), java_iterator(java_iterator), current_entry(env, nullptr) {
        jclass cls = env->FindClass("java/util/Iterator");
        has_next_method = env->GetMethodID(cls, "hasNext", "()Z");
        next_method = env->GetMethodID(cls, "next", "()Ljava/lang/Object;");

        if (this->java_iterator && this->has_next()) {
            this->current_entry = this->next();
        }
    }

    ::iterator &operator++() {
        if (this->java_iterator && this->has_next()) {
            this->current_entry = this->next();
        } else {
            this->java_iterator = nullptr;
            this->current_entry = ::entry(env, nullptr);
        }
        return *this;
    }

    bool operator!=(const iterator &other) const {
        return this->java_iterator != other.java_iterator;
    }

    ::entry &operator*() {
        return this->current_entry;
    }

private:
    JNIEnv *env;
    jobject java_iterator;
    ::entry current_entry;
    jmethodID has_next_method;
    jmethodID next_method;

    bool has_next() {
        return (bool)env->CallBooleanMethod(java_iterator, has_next_method);
    }

    ::entry next() {
        return ::entry(env, env->CallObjectMethod(java_iterator, next_method));
    }
};

class entry_set {
public:
    entry_set(JNIEnv *env, jobject java_entry_set) : env(env), java_entry_set(java_entry_set) {
        jclass cls = env->FindClass("java/util/Set");
        iterator_method = env->GetMethodID(cls, "iterator", "()Ljava/util/Iterator;");
    }

    ::iterator begin() {
        return this->iterator();
    }

    ::iterator end() {
        return ::iterator(env, nullptr);
    }

private:
    JNIEnv *env;
    jobject java_entry_set;
    jmethodID iterator_method;

    ::iterator iterator() {
        return ::iterator(env, env->CallObjectMethod(java_entry_set, iterator_method));
    }
};

class map {
public:
    map(JNIEnv *env, jobject java_map) : env(env), java_map(java_map) {
        jclass cls = env->FindClass("java/util/Map");
        entry_set_method = env->GetMethodID(cls, "entrySet", "()Ljava/util/Set;");
    }

    ::entry_set entry_set() {
        return ::entry_set(env, env->CallObjectMethod(java_map, entry_set_method));
    }

private:
    JNIEnv *env;
    jobject java_map;
    jmethodID entry_set_method;
};

}  // namespace

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonDict_from(JNIEnv *env, jclass, jobject java_map) {
    const GIL gil;

    if (!java_map) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java map cannot be null"));
        return nullptr;
    }

    PyObject *py_dict = PyDict_New();
    if (!py_dict) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    for (auto &entry : map(env, java_map).entry_set()) {
        jobject java_key = entry.key();
        jobject java_value = entry.value();

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
            env->Throw(java_traits<python_exception>::create(env));
            Py_DecRef(py_dict);
            return nullptr;
        }
    }

    return java_traits<python_dict>::convert(env, py_dict);
}
