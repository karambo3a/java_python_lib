#include "headers/python_object_factory.h"
#include "headers/globals.h"


jobject create_java_object(JNIEnv* env, jclass cls, std::size_t index){
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jobject java_py_object = env->NewObject(cls, constructor, index);
    return java_py_object;
}


jobject create_python_object(JNIEnv* env, std::size_t index){
    jclass python_object_class = env->FindClass("org/python/integration/object/PythonObject");
    return create_java_object(env, python_object_class, index);
}


jobject create_python_int(JNIEnv* env, std::size_t index){
    jclass python_int_class = env->FindClass("org/python/integration/object/PythonInt");
    return create_java_object(env, python_int_class, index);
}


jobject create_python_callable(JNIEnv* env, std::size_t index){
    jclass python_callable_class = env->FindClass("org/python/integration/object/PythonCallable");
    return create_java_object(env, python_callable_class, index);
}


// TODO
jobject convert_to_java_object(JNIEnv* env, PyObject* py_object) {
    if (!py_object) {
        return nullptr;
    }
    std::size_t index = object_manager->add_object(py_object);
    jobject java_object = create_python_object(env, index);
    return java_object;
}


jthrowable create_python_exception(JNIEnv* env) {
    PyObject *py_type, *py_value, *py_traceback;
    #if PYTHON_VERSION >= 312
        PyObject* py_exception = PyErr_GetRaisedException();
        py_type = PyObject_Type(py_exception);
        py_value = PyException_GetArgs(py_exception);
        py_traceback = PyException_GetTraceback(py_exception);
        Py_XDECREF(py_exception);
    #else
        PyErr_Fetch(&py_type, &py_value, &py_traceback);
    #endif

    jobject java_type = convert_to_java_object(env, py_type);
    jobject java_value = convert_to_java_object(env, py_value);
    jobject java_traceback = convert_to_java_object(env, py_traceback);
    Py_XDECREF(py_type);
    Py_XDECREF(py_value);
    Py_XDECREF(py_traceback);

    jclass python_exception_class = env->FindClass("org/python/integration/exception/PythonException");
    jmethodID constructor = env->GetMethodID(python_exception_class, "<init>", "(Lorg/python/integration/object/IPythonObject;Lorg/python/integration/object/IPythonObject;Lorg/python/integration/object/IPythonObject;)V");
    jthrowable java_exception = (jthrowable)env->NewObject(python_exception_class, constructor, java_type, java_value, java_traceback);
    return java_exception;
}


jthrowable create_native_operation_exception(JNIEnv* env, const char message) {
    jclass python_exception_class = env->FindClass("org/python/integration/exception/NativeOperationException");
    jmethodID constructor = env->GetMethodID(python_exception_class, "<init>", "(Ljava/lang/String;)V");
    jthrowable java_exception = (jthrowable)env->NewObject(python_exception_class, constructor, message);
    return java_exception;
}
