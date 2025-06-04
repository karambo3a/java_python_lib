#include "org_python_integration_object_PythonCallable.h"
#include "gil.h"
#include "globals.h"
#include "py_java_function.h"
#include "traits.h"

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_PythonCallable_call(JNIEnv *env, jobject java_object, jobjectArray jargs) {
    GIL gil;

    PyObject *callable_object = object_manager->get_object(env, java_object);
    if (!callable_object) {
        return nullptr;
    }

    const jsize args_cnt = env->GetArrayLength(jargs);
    PyObject *args = PyTuple_New((Py_ssize_t)args_cnt);
    if (!args) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Failed to create tuple for arguments"));
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
        env->Throw(java_traits<python_exception>::create(env));
        Py_DecRef(args);
        return nullptr;
    }

    return java_traits<python_object>::convert(env, func_result);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Consumer_2(
    JNIEnv *env,
    jclass,
    jobject java_consumer
) {
    GIL gil;

    if (!java_consumer) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java consumer cannot be null"));
        return nullptr;
    }

    jclass consumer_class = env->FindClass("java/util/function/Consumer");
    PyJavaFunctionObject *py_java_consumer = create_py_java_function_object(
        env, java_consumer, env->GetMethodID(consumer_class, "accept", "(Ljava/lang/Object;)V"), 1, true
    );

    return java_traits<python_callable>::convert(env, (PyObject *)py_java_consumer);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Supplier_2(
    JNIEnv *env,
    jclass,
    jobject java_supplier
) {
    GIL gil;

    if (!java_supplier) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java supplier cannot be null"));
        return nullptr;
    }

    jclass supplier_class = env->FindClass("java/util/function/Supplier");
    PyJavaFunctionObject *py_java_supplier = create_py_java_function_object(
        env, java_supplier, env->GetMethodID(supplier_class, "get", "()Ljava/lang/Object;"), 0
    );

    return java_traits<python_callable>::convert(env, (PyObject *)py_java_supplier);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_Function_2(
    JNIEnv *env,
    jclass,
    jobject java_function
) {
    GIL gil;

    if (!java_function) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java function cannot be null"));
        return nullptr;
    }

    jclass function_class = env->FindClass("java/util/function/Function");
    PyJavaFunctionObject *py_java_function = create_py_java_function_object(
        env, java_function, env->GetMethodID(function_class, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;"), 1
    );

    return java_traits<python_callable>::convert(env, (PyObject *)py_java_function);
}

JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonCallable_from__Ljava_util_function_BiFunction_2(
    JNIEnv *env,
    jclass,
    jobject java_bi_function
) {
    GIL gil;

    if (!java_bi_function) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java bi function cannot be null"));
        return nullptr;
    }

    jclass bi_function_class = env->FindClass("java/util/function/BiFunction");
    PyJavaFunctionObject *py_java_bi_function = create_py_java_function_object(
        env, java_bi_function,
        env->GetMethodID(bi_function_class, "apply", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), 2
    );

    return java_traits<python_callable>::convert(env, (PyObject *)py_java_bi_function);
}

JNIEXPORT jobject JNICALL
Java_org_python_integration_object_PythonCallable_from__Lorg_python_integration_object_PythonCallable_00024Function3_2(
    JNIEnv *env,
    jclass,
    jobject java_function3
) {
    GIL gil;

    if (!java_function3) {
        env->Throw(java_traits<native_operation_exception>::create(env, "Java function3 cannot be null"));
        return nullptr;
    }

    jclass function3_class = env->FindClass("org/python/integration/object/PythonCallable$Function3");
    PyJavaFunctionObject *py_java_function3 = create_py_java_function_object(
        env, java_function3,
        env->GetMethodID(
            function3_class, "apply", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        3
    );

    return java_traits<python_callable>::convert(env, (PyObject *)py_java_function3);
}
