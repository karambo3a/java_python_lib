#ifndef PY_JAVA_FUNCTION_H
#define PY_JAVA_FUNCTION_H

#include <Python.h>
#include <jni.h>

typedef struct {
    PyObject_HEAD
    JavaVM *java_vm;
    jobject java_function;
    jmethodID java_method;
    std::size_t args_cnt;
    bool is_void;
} PyJavaFunctionObject;

void py_java_function_dealloc(PyObject *self);

PyObject *py_java_function_call(PyObject *self, PyObject *args, PyObject *kwargs);

inline PyTypeObject PyJavaFunction_Type = {
    .tp_name = "PyJavaFunctionObject",
    .tp_basicsize = sizeof(PyJavaFunctionObject),
    .tp_dealloc = (destructor)py_java_function_dealloc,
    .tp_call = (ternaryfunc)py_java_function_call,
    .tp_flags = Py_TPFLAGS_DEFAULT,
    .tp_new = PyType_GenericNew,
};

PyJavaFunctionObject *create_py_java_function_object(
    JNIEnv *env,
    jobject java_function,
    jmethodID java_method,
    std::size_t args_cnt,
    bool is_void = false
);

#endif  // PY_JAVA_FUNCTION_H
