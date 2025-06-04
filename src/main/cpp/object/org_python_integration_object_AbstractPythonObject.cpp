#include "org_python_integration_object_AbstractPythonObject.h"
#include "gil.h"
#include "globals.h"
#include "traits.h"
#include <iostream>

JNIEXPORT jboolean JNICALL
Java_org_python_integration_object_AbstractPythonObject_equals(JNIEnv *env, jobject this_object, jobject other_object) {
    const GIL gil;

    if (this_object == other_object) {
        return JNI_TRUE;
    }

    PyObject *py_this = object_manager->get_object(env, this_object);
    PyObject *py_other = object_manager->get_object(env, other_object);
    if (!py_this || !py_other) {
        return JNI_FALSE;
    }
    const int result = PyObject_RichCompareBool(py_this, py_other, Py_EQ);
    if (result == -1) {
        env->Throw(java_traits<python_exception>::create(env));
        return JNI_FALSE;
    } else if (result == 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL
Java_org_python_integration_object_AbstractPythonObject_hashCode(JNIEnv *env, jobject java_object) {
    const GIL gil;

    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return -1;
    }
    const Py_hash_t hash = PyObject_Hash(py_object);
    if (hash == -1 && PyErr_Occurred()) {
        env->Throw(java_traits<python_exception>::create(env));
    }
    return (jint)hash;
}

JNIEXPORT jstring JNICALL
Java_org_python_integration_object_AbstractPythonObject_toString(JNIEnv *env, jobject java_object) {
    const GIL gil;

    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    PyObject *py_str = PyObject_Str(py_object);
    if (!py_str) {
        env->Throw(java_traits<python_exception>::create(env));
    }
    const char *repr = PyUnicode_AsUTF8(py_str);
    jstring java_string = nullptr;
    if (!repr) {
        env->Throw(java_traits<python_exception>::create(env));
        Py_DecRef(py_object);
    } else {
        java_string = env->NewStringUTF(repr);
    }
    Py_DecRef(py_str);
    return java_string;
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_keepAlive(JNIEnv *env, jobject java_object) {
    const GIL gil;

    const std::size_t scope_id = get_scope(env, java_object);

    PythonObjectManager *curr_object_manager = object_manager;
    while (curr_object_manager->get_prev_object_manager() != nullptr && curr_object_manager->get_scope_id() != scope_id
    ) {
        curr_object_manager = curr_object_manager->get_prev_object_manager();
    }
    PyObject *py_object = curr_object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }

    PythonObjectManager *prev_object_manager = curr_object_manager->get_prev_object_manager();
    if (!prev_object_manager) {
        env->Throw(java_traits<native_operation_exception>::create(
            env, "Cannot move object to higher scope: already in root scope"
        ));
        return nullptr;
    }

    const std::size_t index = prev_object_manager->add_object(py_object, true);
    return java_traits<python_object>::create(env, index, (jlong)prev_object_manager->get_scope_id());
}

JNIEXPORT jstring JNICALL
Java_org_python_integration_object_AbstractPythonObject_representation(JNIEnv *env, jobject java_object) {
    const GIL gil;

    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }
    PyObject *py_repr = PyObject_Repr(py_object);
    if (!py_repr) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    // TODO: change PyUnicode_AsUTF8 to PyUnicode_AsUTF8AndSize
    const char *repr = PyUnicode_AsUTF8(py_repr);
    if (!repr) {
        env->Throw(java_traits<python_exception>::create(env));
        Py_DecRef(py_repr);
        return nullptr;
    }
    jstring java_string = env->NewStringUTF(repr);
    Py_DecRef(py_repr);
    return java_string;
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_getAttribute(JNIEnv *env, jobject java_object, jstring name) {
    const GIL gil;

    if (!name) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Attribute name cannot be null"));
        return nullptr;
    }

    PyObject *py_object = object_manager->get_object(env, java_object);
    if (!py_object) {
        return nullptr;
    }

    const char *attr_name = env->GetStringUTFChars(name, nullptr);
    if (!attr_name) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Failed to convert JavaString to const char*"));
        return nullptr;
    }

    PyObject *attr_object = PyObject_GetAttrString(py_object, attr_name);
    env->ReleaseStringUTFChars(name, attr_name);

    if (!attr_object) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    return java_traits<python_object>::convert(env, attr_object);
}

namespace {
static jobject optional_empty(JNIEnv *env) {
    jclass optional_class = env->FindClass("java/util/Optional");
    jmethodID empty_method = env->GetStaticMethodID(optional_class, "empty", "()Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, empty_method);
}

static jobject optional_of(JNIEnv *env, jobject java_py_object) {
    jclass optional_class = env->FindClass("java/util/Optional");
    jmethodID of_method = env->GetStaticMethodID(optional_class, "of", "(Ljava/lang/Object;)Ljava/util/Optional;");
    return env->CallStaticObjectMethod(optional_class, of_method, java_py_object);
}

template <typename T>
jobject AbstractPythonObject_asT(JNIEnv *env, jobject java_object) {
    const GIL gil;

    const std::size_t index = get_index(env, java_object);
    const std::size_t scope_id = get_scope(env, java_object);
    PyObject *py_object = object_manager->get_object(env, index, scope_id);
    if (!py_object) {
        return nullptr;
    }

    if (!python_traits<T>::check(py_object)) {
        return optional_empty(env);
    }

    jobject java_py_object = java_traits<T>::create(env, index, scope_id);
    return optional_of(env, java_py_object);
}
}  // namespace

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asCallable(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_callable>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asInt(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_int>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asFloat(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_float>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asBool(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_bool>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asStr(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_str>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asList(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_list>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asDict(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_dict>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asTuple(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_tuple>(env, java_object);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_AbstractPythonObject_asSet(JNIEnv *env, jobject java_object) {
    return AbstractPythonObject_asT<python_set>(env, java_object);
}
