#include "org_python_integration_core_PythonCore.h"
#include "gil.h"
#include "globals.h"
#include "traits.h"
#include <Python.h>
#include <unordered_map>

JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_evaluate(JNIEnv *env, jclass, jstring java_repr) {
    const GIL gil;

    PyObject *main_module = PyImport_AddModule("__main__");
    if (!main_module) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    PyObject *pdict = PyModule_GetDict(main_module);
    if (!pdict) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    PyObject *pdict_new = PyDict_New();
    if (!pdict_new) {
        env->Throw(java_traits<python_exception>::create(env));
        return nullptr;
    }

    const char *repr = env->GetStringUTFChars(java_repr, nullptr);
    PyObject *py_object = PyRun_String(repr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(java_repr, repr);

    if (!py_object) {
        env->Throw(java_traits<python_exception>::create(env));
        Py_DecRef(pdict_new);
        return nullptr;
    }
    return java_traits<python_object>::convert(env, py_object);
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonCore_free(JNIEnv *env, jclass, jobject java_object) {
    const GIL gil;

    PythonObjectManager::free_object(env, java_object);
}

namespace {
jobject import_module(JNIEnv *env, jstring java_module) {
    if (!java_module) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Module name cannot be null"));
        return nullptr;
    }

    const char *module_name = env->GetStringUTFChars(java_module, nullptr);

    PyObject *py_module = PyImport_ImportModule(module_name);
    if (!py_module) {
        env->Throw(java_traits<python_exception>::create(env));
        env->ReleaseStringUTFChars(java_module, module_name);
        return nullptr;
    }
    env->ReleaseStringUTFChars(java_module, module_name);
    return java_traits<python_object>::convert(env, py_module);
}

void free_pyobject_map(std::unordered_map<jstring, PyObject *> &names) {
    for (auto &name_and_attr : names) {
        Py_DecRef(name_and_attr.second);
    }
}
}  // namespace

class map {
public:
    map(JNIEnv *env, std::size_t names_size) : env(env) {
        jclass map_class = env->FindClass("java/util/HashMap");
        jmethodID map_constructor = env->GetMethodID(map_class, "<init>", "(I)V");
        this->java_map = env->NewObject(map_class, map_constructor, (jsize)names_size);
        this->put_method = env->GetMethodID(map_class, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    }

    void put(jstring name, PyObject *attr) {
        env->CallObjectMethod(this->java_map, this->put_method, name, java_traits<python_object>::convert(env, attr));
    }

    jobject get() {
        return this->java_map;
    }

private:
    JNIEnv *env;
    jobject java_map;
    jmethodID put_method;
};

JNIEXPORT jobject JNICALL
Java_org_python_integration_core_PythonCore_importModule(JNIEnv *env, jclass, jstring java_module) {
    const GIL gil;

    return import_module(env, java_module);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_core_PythonCore_fromImport(JNIEnv *env, jclass, jstring java_from, jobjectArray java_names) {
    const GIL gil;

    if (!java_names) {
        env->Throw(java_traits<native_operation_exception>::create(env, "names cannot be null"));
        return nullptr;
    }

    const std::size_t names_size = (std::size_t)env->GetArrayLength(java_names);
    if (names_size < 1) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Must be at least one name"));
        return nullptr;
    }

    jobject java_module = import_module(env, java_from);
    if (!java_module) {
        return nullptr;
    }
    PyObject *py_module = object_manager->get_object(env, java_module);

    std::unordered_map<jstring, PyObject *> names;
    for (std::size_t i = 0; i < names_size; ++i) {
        jstring java_name = (jstring)env->GetObjectArrayElement(java_names, (jsize)i);
        if (!java_name) {
            env->Throw(java_traits<native_operation_exception>::create(env, "Name cannot be null"));
            free_pyobject_map(names);
            return nullptr;
        }
        const char *name = env->GetStringUTFChars(java_name, nullptr);
        PyObject *py_attr = PyObject_GetAttrString(py_module, name);
        env->ReleaseStringUTFChars(java_name, name);
        if (!py_attr) {
            env->Throw(java_traits<python_exception>::create(env));
            free_pyobject_map(names);
            return nullptr;
        }
        names.insert({java_name, py_attr});
    }

    map java_map(env, names_size);
    for (auto &[name, attr] : names) {
        java_map.put(name, attr);
    }
    return java_map.get();
}
