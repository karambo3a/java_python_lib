#include "headers/py_java_function.h"
#include "headers/globals.h"
#include "headers/python_object_factory.h"
#include <iostream>
#include <string>

PyObject *py_java_function_call(PyObject *self, PyObject *args, PyObject *kwargs) {
    PyJavaFunctionObject *py_function = (PyJavaFunctionObject *)self;
    JNIEnv *env = nullptr;

    if (py_function->java_vm->AttachCurrentThread((void **)&env, nullptr) != JNI_OK) {
        PyErr_SetString(PyExc_RuntimeError, "Failed to attach to Java VM");
        return nullptr;
    }

    if (kwargs) {
        PyErr_SetString(PyExc_RuntimeError, "kwargs must be null");
        return nullptr;
    }

    if ((std::size_t)PyTuple_Size(args) != py_function->args_cnt) {
        std::string message = "Wrong number of arguments: must be " + std::to_string(py_function->args_cnt) + " arguments";
        PyErr_SetString(PyExc_RuntimeError, message.c_str());
        return nullptr;
    }

    initialize_scope();

    std::vector<jvalue> jargs(py_function->args_cnt);
    for (std::size_t i = 0; i < py_function->args_cnt; ++i) {
        jargs[i].l = convert_to_python_object(env, PyTuple_GetItem(args, (Py_ssize_t)i));
    }

    PyObject *py_result = nullptr;
    if (py_function->is_void) {
        env->CallVoidMethodA(py_function->java_function, py_function->java_method, &jargs[0]);
        py_result = Py_None;
    } else {
        jobject java_result = env->CallObjectMethodA(py_function->java_function, py_function->java_method, &jargs[0]);
        py_result = object_manager->get_object(env, java_result);
    }

    py_function->java_vm->DetachCurrentThread();

    finalize_scope();
    Py_IncRef(py_result);
    return py_result;
}

void py_java_function_dealloc(PyObject *self) {
    PyJavaFunctionObject *py_function = (PyJavaFunctionObject *)self;
    JNIEnv *env = nullptr;
    if (py_function->java_vm && py_function->java_vm->AttachCurrentThread((void **)&env, nullptr) != JNI_OK) {
        Py_TYPE(py_function)->tp_free(py_function);
        return;
    }
    env->DeleteGlobalRef(py_function->java_function);
    py_function->java_function = nullptr;
    py_function->java_vm->DetachCurrentThread();

    Py_TYPE(py_function)->tp_free(py_function);
}

PyJavaFunctionObject *create_py_java_function_object(
    JNIEnv *env,
    jobject java_function,
    jmethodID java_method,
    std::size_t args_cnt,
    bool is_void
) {
    if (PyType_Ready(&PyJavaFunction_Type) < 0) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    PyJavaFunctionObject *py_java_function =
        (PyJavaFunctionObject *)(PyJavaFunction_Type.tp_new(&PyJavaFunction_Type, nullptr, nullptr));
    if (!py_java_function) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }

    if (env->GetJavaVM(&(py_java_function->java_vm)) != JNI_OK) {
        jthrowable java_exception = create_native_operation_exception(env, "Failed to get Java VM");
        env->Throw(java_exception);
        Py_DecRef((PyObject *)py_java_function);
        return nullptr;
    }
    py_java_function->java_function = env->NewGlobalRef(java_function);
    if (!py_java_function->java_function) {
        jthrowable java_exception = create_native_operation_exception(env, "Failed to create global reference");
        env->Throw(java_exception);
        Py_DecRef((PyObject *)py_java_function);
        return nullptr;
    }
    py_java_function->java_method = java_method;
    py_java_function->args_cnt = args_cnt;
    py_java_function->is_void = is_void;
    return py_java_function;
}
