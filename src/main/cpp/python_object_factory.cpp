#include "headers/python_object_factory.h"


jobject create_java_object(JNIEnv* env, jclass cls, std::size_t index){
    jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
    jobject java_py_object = env->NewObject(cls, constructor, index);
    return java_py_object;
}

jobject create_python_object(JNIEnv* env, std::size_t index){
    jclass python_object_class = env->FindClass("org/python/integration/PythonObject");
    return create_java_object(env, python_object_class, index);
}

jobject create_python_int(JNIEnv* env, std::size_t index){
    jclass python_int_class = env->FindClass("org/python/integration/PythonInt");
    return create_java_object(env, python_int_class, index);
}

jobject create_python_callable(JNIEnv* env, std::size_t index){
    jclass python_callable_class = env->FindClass("org/python/integration/PythonCallable");
    return create_java_object(env, python_callable_class, index);
}

