#include "../headers/org_python_integration_object_PythonCallable.h"
#include "../headers/globals.h"
#include "../headers/java_object_factory.h"
#include "../headers/py_java_function.h"

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_PythonCallable_call(JNIEnv *env, jobject java_object, jobjectArray jargs) {
    PyObject *callable_object = object_manager->get_object(env, java_object);
    if (!callable_object) {
        return nullptr;
    }

    const jsize args_cnt = env->GetArrayLength(jargs);
    PyObject *args = PyTuple_New((Py_ssize_t)args_cnt);
    if (!args) {
        jthrowable java_exception = create_native_operation_exception(env, "Failed to create tuple for arguments");
        env->Throw(java_exception);
        return nullptr;
    }

    PyObject *func_result;
    if (args_cnt == 0) {
        func_result = PyObject_CallNoArgs(callable_object);
    } else {
        for (int i = 0; i < args_cnt; ++i) {
            PyObject *arg = object_manager->get_object(env, env->GetObjectArrayElement(jargs, i));
            if (!arg) {
                return nullptr;
            }
            Py_IncRef(arg);
            PyTuple_SetItem(args, i, arg);
        }
        func_result = PyObject_Call(callable_object, args, nullptr);
    }

    if (!func_result) {
        jthrowable java_exception = create_python_exception(env);
        Py_DecRef(args);
        env->Throw(java_exception);
        return nullptr;
    }

    return convert_to_python_object(env, func_result);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Consumer_2(
    JNIEnv *env,
    jclass,
    jobject java_consumer
) {
    if (!java_consumer) {
        jthrowable java_exception = create_native_operation_exception(env, "Java consumer cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jclass consumer_class = env->FindClass("java/util/function/Consumer");
    PyJavaFunctionObject *py_java_consumer = create_py_java_function_object(
        env, java_consumer, env->GetMethodID(consumer_class, "accept", "(Ljava/lang/Object;)V"), 1, true
    );

    return convert_to_python_callable(env, (PyObject *)py_java_consumer);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Supplier_2(
    JNIEnv *env,
    jclass,
    jobject java_supplier
) {
    if (!java_supplier) {
        jthrowable java_exception = create_native_operation_exception(env, "Java supplier cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jclass supplier_class = env->FindClass("java/util/function/Supplier");
    PyJavaFunctionObject *py_java_supplier = create_py_java_function_object(
        env, java_supplier, env->GetMethodID(supplier_class, "get", "()Ljava/lang/Object;"), 0
    );

    return convert_to_python_callable(env, (PyObject *)py_java_supplier);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Function_2(
    JNIEnv *env,
    jclass,
    jobject java_function
) {
    if (!java_function) {
        jthrowable java_exception = create_native_operation_exception(env, "Java function cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jclass function_class = env->FindClass("java/util/function/Function");
    PyJavaFunctionObject *py_java_function = create_py_java_function_object(
        env, java_function, env->GetMethodID(function_class, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;"), 1
    );

    return convert_to_python_callable(env, (PyObject *)py_java_function);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_BiFunction_2(
    JNIEnv *env,
    jclass,
    jobject java_bi_function
) {
    if (!java_bi_function) {
        jthrowable java_exception = create_native_operation_exception(env, "Java bi function cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jclass bi_function_class = env->FindClass("java/util/function/BiFunction");
    PyJavaFunctionObject *py_java_bi_function = create_py_java_function_object(
        env, java_bi_function,
        env->GetMethodID(bi_function_class, "apply", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), 2
    );

    return convert_to_python_callable(env, (PyObject *)py_java_bi_function);
}


JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Lorg_python_integration_object_PythonCallable_00024Function3_2(JNIEnv *env, jclass, jobject java_function3) {
    if (!java_function3) {
        jthrowable java_exception = create_native_operation_exception(env, "Java function3 cannot be null");
        env->Throw(java_exception);
        return nullptr;
    }

    jclass function3_class = env->FindClass("org/python/integration/object/PythonCallable$Function3");
    PyJavaFunctionObject *py_java_function3 = create_py_java_function_object(
        env, java_function3,
        env->GetMethodID(function3_class, "apply", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), 3
    );

    return convert_to_python_callable(env, (PyObject *)py_java_function3);
}
