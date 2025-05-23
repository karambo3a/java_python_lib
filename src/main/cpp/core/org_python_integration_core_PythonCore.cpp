#include "../headers/org_python_integration_core_PythonCore.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"
#include <Python.h>
#include <unordered_map>

JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_evaluate(JNIEnv *env, jclass, jstring java_repr) {
    PyObject *main_module = PyImport_AddModule("__main__");
    if (!main_module) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject *pdict = PyModule_GetDict(main_module);
    if (!pdict) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject *pdict_new = PyDict_New();
    if (!pdict_new) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    const char *repr = env->GetStringUTFChars(java_repr, nullptr);
    PyObject *py_object = PyRun_String(repr, Py_eval_input, pdict, pdict_new);
    env->ReleaseStringUTFChars(java_repr, repr);

    if (!py_object) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(pdict_new);
        env->Throw(java_exception);
        return nullptr;
    }
    return convert_to_python_object(env, py_object);
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonCore_free(JNIEnv *env, jclass, jobject java_object) {
    object_manager->free_object(env, java_object);
}

namespace {
jobject import_module(JNIEnv *env, jstring java_module) {
    if (!java_module) {
        jthrowable java_exception = create_native_operation_exception(env, "Module name cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    const char *module_name = env->GetStringUTFChars(java_module, nullptr);

    PyObject *py_module = PyImport_ImportModule(module_name);
    if (!py_module) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        env->ReleaseStringUTFChars(java_module, module_name);
        return nullptr;
    }
    env->ReleaseStringUTFChars(java_module, module_name);
    return convert_to_python_object(env, py_module);
}

void free_pyobject_map(std::unordered_map<jstring, PyObject *> &names) {
    for (auto &name_and_attr : names) {
        Py_DecRef(name_and_attr.second);
    }
}
}  // namespace

JNIEXPORT jobject JNICALL
Java_org_python_integration_core_PythonCore_importModule(JNIEnv *env, jclass, jstring java_module) {
    return import_module(env, java_module);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_fromImport(
    JNIEnv *env,
    jclass,
    jstring java_from,
    jobjectArray java_names
) {
    if (!java_names) {
        jthrowable java_exception = create_native_operation_exception(env, "names cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    const std::size_t names_size = (std::size_t)env->GetArrayLength(java_names);
    if (names_size < 1) {
        jthrowable java_exception = create_native_operation_exception(env, "Must be at least one name");
        env->Throw(java_exception);
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
            jthrowable java_exception = create_native_operation_exception(env, "Name cannot be null");
            env->Throw(java_exception);
            free_pyobject_map(names);
            return nullptr;
        }
        const char *name = env->GetStringUTFChars(java_name, nullptr);
        PyObject *py_attr = PyObject_GetAttrString(py_module, name);
        env->ReleaseStringUTFChars(java_name, name);
        if (!py_attr) {
            jthrowable java_exception = create_python_exception(env);
            env->Throw(java_exception);
            free_pyobject_map(names);
            return nullptr;
        }
        names.insert({java_name, py_attr});
    }

    jclass map_class = env->FindClass("java/util/HashMap");
    jmethodID map_constructor = env->GetMethodID(map_class, "<init>", "(I)V");
    jobject map = env->NewObject(map_class, map_constructor, (jsize)names_size);
    jmethodID map_put = env->GetMethodID(map_class, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    for (auto &name_and_attr : names) {
        env->CallObjectMethod(map, map_put, name_and_attr.first, convert_to_python_object(env, name_and_attr.second));
    }
    return map;
}
