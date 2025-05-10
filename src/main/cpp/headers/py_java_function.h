#ifndef PY_JAVA_FUNCTION_H
#define PY_JAVA_FUNCTION_H

#include <jni.h>
#include <Python.h>

typedef struct {
    PyObject_HEAD
    JavaVM* java_vm;
    jobject java_function;
    jmethodID java_method;
    std::size_t args_cnt;
    bool is_void;
} PyJavaFunctionObject;

void py_java_function_free(PyObject *self);

PyObject *py_java_function_call(PyObject *self, PyObject *args, PyObject *kwargs);

inline PyTypeObject PyJavaFunction_Type = {
    .ob_base = PyVarObject_HEAD_INIT(NULL, 0)
    .tp_name = "PyJavaFunctionObject",
    .tp_basicsize = sizeof(PyJavaFunctionObject),
    .tp_dealloc = (destructor)py_java_function_free,
    .tp_call = (ternaryfunc)py_java_function_call,
    .tp_flags = Py_TPFLAGS_DEFAULT,
    .tp_new = PyType_GenericNew,
};

PyJavaFunctionObject *create_py_java_function_object(JNIEnv *env, jobject java_function, jmethodID java_method, std::size_t args_cnt, bool is_void=false);

#if JNI_VERSION == 11
    #define JNI_VERSION JNI_VERSION_1_1
#elif JNI_VERSION == 12
    #define JNI_VERSION JNI_VERSION_1_2
#elif JNI_VERSION == 14
    #define JNI_VERSION JNI_VERSION_1_4
#elif JNI_VERSION == 16
    #define JNI_VERSION JNI_VERSION_1_6
#elif JNI_VERSION == 18
    #define JNI_VERSION JNI_VERSION_1_8
#elif JNI_VERSION == 9
    #define JNI_VERSION JNI_VERSION_9
#elif JNI_VERSION == 10
    #define JNI_VERSION JNI_VERSION_10
#elif JNI_VERSION == 19
    #define JNI_VERSION JNI_VERSION_19
#elif JNI_VERSION == 20
    #define JNI_VERSION JNI_VERSION_20
#elif JNI_VERSION == 21
    #define JNI_VERSION JNI_VERSION_21
#else
    #error "Unsupported JNI_VERSION specified"
#endif

#endif // PY_JAVA_FUNCTION_H
